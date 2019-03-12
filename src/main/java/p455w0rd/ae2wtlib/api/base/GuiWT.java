package p455w0rd.ae2wtlib.api.base;

import java.io.IOException;
import java.text.*;
import java.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.widgets.ITooltip;
import appeng.client.me.*;
import appeng.container.slot.*;
import appeng.container.slot.AppEngSlot.hasCalculatedValidness;
import appeng.core.AEConfig;
import appeng.core.localization.ButtonToolTips;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem;
import p455w0rd.ae2wtlib.api.WTApi.Integration.Mods;
import p455w0rd.ae2wtlib.api.client.IWTGuiScrollbar;
import p455w0rd.ae2wtlib.container.slot.SlotOutput;
import p455w0rd.ae2wtlib.container.slot.SlotPlayerHotBar;
import p455w0rd.ae2wtlib.integration.JEI;

public abstract class GuiWT extends GuiContainer {

	protected static boolean switchingGuis;
	public List<InternalSlotME> meSlots = new LinkedList<InternalSlotME>();
	// drag y
	protected final Set<Slot> drag_click = new HashSet<Slot>();
	private IWTGuiScrollbar scrollBar = null;
	protected boolean disableShiftClick = false;
	protected Stopwatch dbl_clickTimer = Stopwatch.createStarted();
	protected ItemStack dbl_whichItem;
	protected Slot bl_clicked;
	public boolean subGui;
	//protected final FluidStackSizeRenderer fluidStackSizeRenderer = new FluidStackSizeRenderer();

	public GuiWT(final Container container) {
		super(container);
		subGui = switchingGuis;
		switchingGuis = false;
	}

	public ItemStack getWirelessTerminal() {
		return ((ContainerWT) inventorySlots).getWirelessTerminal();
	}

	protected static String join(final Collection<String> toolTip, final String delimiter) {
		final Joiner joiner = Joiner.on(delimiter);

		return joiner.join(toolTip);
	}

	protected int getQty(final GuiButton btn) {
		try {
			final DecimalFormat df = new DecimalFormat("+#;-#");
			return df.parse(btn.displayString).intValue();
		}
		catch (final ParseException e) {
			return 0;
		}
	}

	public boolean isSubGui() {
		return subGui;
	}

	@Override
	public void initGui() {
		super.initGui();

		final List<Slot> slots = getInventorySlots();
		final Iterator<Slot> i = slots.iterator();
		while (i.hasNext()) {
			if (i.next() instanceof SlotME) {
				i.remove();
			}
		}

		for (final InternalSlotME me : meSlots) {
			slots.add(new SlotME(me));
		}
	}

	protected List<Slot> getInventorySlots() {
		return inventorySlots.inventorySlots;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, final float partialTicks) {
		super.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
		drawButtonTooltip(mouseX, mouseY);
	}

	protected void drawButtonTooltip(int mouseX, int mouseY) {
		for (final Object c : buttonList) {
			if (c instanceof ITooltip) {
				final ITooltip tooltipButton = (ITooltip) c;
				final int x = tooltipButton.xPos();
				int y = tooltipButton.yPos();

				if (x < mouseX && x + tooltipButton.getWidth() > mouseX && tooltipButton.isVisible()) {
					if (y < mouseY && y + tooltipButton.getHeight() > mouseY) {
						if (y < 15) {
							y = 15;
						}

						final String msg = tooltipButton.getMessage();
						if (msg != null) {
							String[] lines = msg.split("\n");
							int tooltipWidth = 0;
							for (String line : lines) {
								if (fontRenderer.getStringWidth(line) > tooltipWidth) {
									tooltipWidth = fontRenderer.getStringWidth(line);
								}
							}
							tooltipWidth += 12;
							if (Mods.JEI.isLoaded() && JEI.isIngrediantOverlayActive() && (x + tooltipWidth > (xSize + guiLeft))) {
								drawTooltip(x - 5 - tooltipWidth, y + 4, msg);
							}
							else {
								this.drawTooltip(x + 11, y + 4, msg);
							}
						}
					}
				}
			}
		}
	}

	protected void drawTooltip(int x, int y, String message) {
		String[] lines = message.split("\n");
		this.drawTooltip(x, y, Arrays.asList(lines));
		GlStateManager.disableLighting();
	}

	protected void drawTooltip(int x, int y, List<String> lines) {
		if (lines.isEmpty()) {
			return;
		}

		// For an explanation of the formatting codes, see http://minecraft.gamepedia.com/Formatting_codes
		lines = Lists.newArrayList(lines); // Make a copy

		// Make the first line white
		lines.set(0, TextFormatting.WHITE + lines.get(0));

		// All lines after the first are colored gray
		for (int i = 1; i < lines.size(); i++) {
			lines.set(i, TextFormatting.GRAY + lines.get(i));
		}

		this.drawHoveringText(lines, x, y, fontRenderer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int xx, final int yy) {
		final int ox = guiLeft;
		final int oy = guiTop;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (getScrollBar() != null) {
			getScrollBar().draw(this);
		}
		drawFG(ox, oy, xx, yy);
	}

	public abstract void drawFG(int offsetX, int offsetY, int mouseX, int mouseY);

	@Override
	protected void drawGuiContainerBackgroundLayer(final float f, final int x, final int y) {
		final int ox = guiLeft;
		final int oy = guiTop;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		drawBG(ox, oy, x, y);

		final List<Slot> slots = getInventorySlots();
		for (final Slot slot : slots) {
			if (slot instanceof OptionalSlotFake) {
				final OptionalSlotFake fs = (OptionalSlotFake) slot;
				if (fs.isRenderDisabled()) {
					if (fs.isEnabled()) {
						this.drawTexturedModalRect(ox + fs.xPos - 1, oy + fs.yPos - 1, fs.getSourceX() - 1, fs.getSourceY() - 1, 18, 18);
					}
					else {
						GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
						GL11.glEnable(GL11.GL_BLEND);
						this.drawTexturedModalRect(ox + fs.xPos - 1, oy + fs.yPos - 1, fs.getSourceX() - 1, fs.getSourceY() - 1, 18, 18);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glPopAttrib();
					}
				}
			}
		}
	}

	@Override
	protected void mouseClicked(final int xCoord, final int yCoord, final int btn) throws IOException {
		drag_click.clear();

		if (btn == 1) {
			for (final Object o : buttonList) {
				final GuiButton guibutton = (GuiButton) o;
				if (guibutton.mousePressed(mc, xCoord, yCoord)) {
					super.mouseClicked(xCoord, yCoord, 0);
					return;
				}
			}
		}

		super.mouseClicked(xCoord, yCoord, btn);
	}

	@Override
	protected void mouseClickMove(final int x, final int y, final int c, final long d) {
		if (getScrollBar() != null) {
			getScrollBar().click(this, x - guiLeft, y - guiTop);
		}
		super.mouseClickMove(x, y, c, d);
	}

	@Override
	protected void handleMouseClick(final Slot slot, final int slotIdx, final int mouseButton, final ClickType clickType) {
		super.handleMouseClick(slot, slotIdx, mouseButton, clickType);
	}

	//disable hotbar key-swapping WCT
	@Override
	protected boolean checkHotbarKeys(final int keyCode) {
		final Slot theSlot = getSlotUnderMouse();
		if (Minecraft.getMinecraft().player.inventory.getItemStack().isEmpty() && theSlot != null) {
			if (theSlot.getStack().getItem() instanceof ICustomWirelessTerminalItem) {
				return false;
			}
			for (int j = 0; j < 9; ++j) {
				if (keyCode == mc.gameSettings.keyBindsHotbar[j].getKeyCode()) {
					final List<Slot> slots = inventorySlots.inventorySlots;
					for (final Slot s : slots) {
						if (s instanceof AppEngSlot && ((AppEngSlot) s).getItemHandler() instanceof InvWrapper) {
							IItemHandler inv = ((AppEngSlot) s).getItemHandler();
							ItemStack pullingStack = inv.getStackInSlot(j);
							if (!(pullingStack.getItem() instanceof ICustomWirelessTerminalItem)) {
								handleMouseClick(theSlot, theSlot.slotNumber, j, ClickType.SWAP);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		subGui = true; // in case the gui is reopened later ( i'm looking at you NEI )
	}

	protected Slot getSlot(final int mouseX, final int mouseY) {
		final List<Slot> slots = getInventorySlots();
		for (final Slot slot : slots) {
			// isPointInRegion
			if (isPointInRegion(slot.xPos, slot.yPos, 16, 16, mouseX, mouseY)) {
				return slot;
			}
		}
		return null;
	}

	public abstract void drawBG(int offsetX, int offsetY, int mouseX, int mouseY);

	protected boolean enableSpaceClicking() {
		return true;
	}

	public void bindTexture(final String base, final String file) {
		final ResourceLocation loc = new ResourceLocation(base, "textures/" + file);
		mc.getTextureManager().bindTexture(loc);
	}

	protected void drawItem(final int x, final int y, final ItemStack is) {
		zLevel = 100.0F;
		itemRender.zLevel = 100.0F;
		GL11.glEnable(GL11.GL_LIGHTING);
		GlStateManager.enableDepth();
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.disableDepth();
		itemRender.renderItemAndEffectIntoGUI(is, x, y);
		itemRender.zLevel = 0.0F;
		zLevel = 0.0F;
	}

	protected String getGuiDisplayName(final String in) {
		return in;//hasCustomInventoryName() ? getInventoryName() : in;
	}

	@Override
	public void drawSlot(final Slot s) {
		/*
		if (s instanceof SlotME) {
			try {
				zLevel = 100.0F;
				itemRender.zLevel = 100.0F;
				if (!isPowered()) {
					drawRect(s.xPos, s.yPos, 16 + s.xPos, 16 + s.yPos, 0x66111111);
				}
				zLevel = 0.0F;
				itemRender.zLevel = 0.0F;
				//super.drawSlot(new SlotSingleItem(s));
				super.drawSlot(new Size1Slot((SlotME) s));
				getStackSizeRenderer().renderStackSize(fontRenderer, ((SlotME) s).getAEStack(), s.xPos, s.yPos);
			}
			catch (final Exception err) {
			}
			return;
		}
		*/
		/* TODO put in WFT
		else if (s instanceof IMEFluidSlot && ((IMEFluidSlot) s).shouldRenderAsFluid()) {
			final IMEFluidSlot slot = (IMEFluidSlot) s;
			final IAEFluidStack fs = slot.getAEFluidStack();
		
			if (fs != null && isPowered()) {
				GlStateManager.disableLighting();
				GlStateManager.disableBlend();
				final Fluid fluid = fs.getFluid();
				Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				final TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());
		
				// Set color for dynamic fluids
				// Convert int color to RGB
				float red = (fluid.getColor() >> 16 & 255) / 255.0F;
				float green = (fluid.getColor() >> 8 & 255) / 255.0F;
				float blue = (fluid.getColor() & 255) / 255.0F;
				GlStateManager.color(red, green, blue);
		
				this.drawTexturedModalRect(s.xPos, s.yPos, sprite, 16, 16);
				GlStateManager.enableLighting();
				GlStateManager.enableBlend();
		
				//if (s instanceof IMEFluidSlot) {
				//final IMEFluidSlot meFluidSlot = (IMEFluidSlot) s;
				//fluidStackSizeRenderer.renderStackSize(fontRenderer, fs, s.xPos, s.yPos);
				//}
			}
			else if (!isPowered()) {
				drawRect(s.xPos, s.yPos, 16 + s.xPos, 16 + s.yPos, 0x66111111);
			}
		
			return;
		}
		*/
		//else {
		try {
			final ItemStack is = s.getStack();
			if (s instanceof AppEngSlot && (((AppEngSlot) s).renderIconWithItem() || is.isEmpty()) && (((AppEngSlot) s).shouldDisplay())) {
				final AppEngSlot aes = (AppEngSlot) s;
				if (aes.getIcon() >= 0) {
					this.bindTexture("gui/states.png");
					final Tessellator tessellator = Tessellator.getInstance();
					final BufferBuilder vb = tessellator.getBuffer();
					try {
						final int uv_y = (int) Math.floor(aes.getIcon() / 16);
						final int uv_x = aes.getIcon() - uv_y * 16;
						GlStateManager.enableBlend();
						GlStateManager.disableLighting();
						GlStateManager.enableTexture2D();
						GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
						final float par1 = aes.xPos;
						final float par2 = aes.yPos;
						final float par3 = uv_x * 16;
						final float par4 = uv_y * 16;
						vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
						final float f1 = 0.00390625F;
						final float f = 0.00390625F;
						final float par6 = 16;
						vb.pos(par1 + 0, par2 + par6, zLevel).tex((par3 + 0) * f, (par4 + par6) * f1).color(1.0f, 1.0f, 1.0f, aes.getOpacityOfIcon()).endVertex();
						final float par5 = 16;
						vb.pos(par1 + par5, par2 + par6, zLevel).tex((par3 + par5) * f, (par4 + par6) * f1).color(1.0f, 1.0f, 1.0f, aes.getOpacityOfIcon()).endVertex();
						vb.pos(par1 + par5, par2 + 0, zLevel).tex((par3 + par5) * f, (par4 + 0) * f1).color(1.0f, 1.0f, 1.0f, aes.getOpacityOfIcon()).endVertex();
						vb.pos(par1 + 0, par2 + 0, zLevel).tex((par3 + 0) * f, (par4 + 0) * f1).color(1.0f, 1.0f, 1.0f, aes.getOpacityOfIcon()).endVertex();
						tessellator.draw();
					}
					catch (final Exception err) {
					}
				}
			}

			if (!is.isEmpty() && s instanceof AppEngSlot) {
				if (((AppEngSlot) s).getIsValid() == hasCalculatedValidness.NotAvailable) {
					boolean isValid = s.isItemValid(is) || s instanceof SlotOutput || s instanceof AppEngCraftingSlot || s instanceof SlotPlayerHotBar || s instanceof SlotDisabled || s instanceof SlotInaccessible || s instanceof SlotFake || s instanceof SlotRestrictedInput || s instanceof SlotDisconnected;
					if (isValid && s instanceof SlotRestrictedInput) {
						try {
							isValid = ((SlotRestrictedInput) s).isValid(is, Minecraft.getMinecraft().world);
						}
						catch (final Exception err) {
						}
					}
					((AppEngSlot) s).setIsValid(isValid ? hasCalculatedValidness.Valid : hasCalculatedValidness.Invalid);
				}

				if (((AppEngSlot) s).getIsValid() == hasCalculatedValidness.Invalid) {
					zLevel = 100.0F;
					itemRender.zLevel = 100.0F;

					GL11.glDisable(GL11.GL_LIGHTING);
					drawRect(s.xPos, s.yPos, 16 + s.xPos, 16 + s.yPos, 0x66ff6666);
					GL11.glEnable(GL11.GL_LIGHTING);

					zLevel = 0.0F;
					itemRender.zLevel = 0.0F;
				}
			}

			if (s instanceof AppEngSlot) {
				((AppEngSlot) s).setDisplay(true);
				super.drawSlot(s);
			}
			else {
				super.drawSlot(s);
			}

			return;
		}
		catch (final Exception err) {
		}
		//}
		// do the usual for non-ME Slots.
		super.drawSlot(s);
	}

	@Override
	protected void renderToolTip(final ItemStack stack, final int x, final int y) {
		final Slot s = getSlot(x, y);
		if (s instanceof SlotME && !stack.isEmpty()) {
			final int bigNumber = AEConfig.instance().useTerminalUseLargeFont() ? 999 : 9999;
			IAEItemStack myStack = null;
			final List<String> currentToolTip = getItemToolTip(stack);
			try {
				final SlotME theSlotField = (SlotME) s;
				myStack = theSlotField.getAEStack();
			}
			catch (final Throwable ignore) {
			}
			if (myStack != null) {
				if (myStack.getStackSize() > bigNumber || (myStack.getStackSize() > 1 && stack.isItemDamaged())) {
					final String local = ButtonToolTips.ItemsStored.getLocal();
					final String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(myStack.getStackSize());
					final String format = String.format(local, formattedAmount);
					currentToolTip.add(TextFormatting.GRAY + format);
				}
				if (myStack.getCountRequestable() > 0) {
					final String local = ButtonToolTips.ItemsRequestable.getLocal();
					final String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(myStack.getCountRequestable());
					final String format = String.format(local, formattedAmount);

					currentToolTip.add(format);
				}
				drawHoveringText(currentToolTip, x, y, fontRenderer);
				return;
			}
			else if (stack.getCount() > bigNumber) {
				final String local = ButtonToolTips.ItemsStored.getLocal();
				final String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(stack.getCount());
				final String format = String.format(local, formattedAmount);
				currentToolTip.add(TextFormatting.GRAY + format);
				this.drawHoveringText(currentToolTip, x, y, fontRenderer);
				return;
			}
		}
		super.renderToolTip(stack, x, y);
	}

	protected boolean isPowered() {
		return true;
	}

	public void bindTexture(final String file) {
		final ResourceLocation loc = new ResourceLocation("appliedenergistics2", "textures/" + file);
		mc.getTextureManager().bindTexture(loc);
	}

	protected IWTGuiScrollbar getScrollBar() {
		return scrollBar;
	}

	protected void setScrollBar(final IWTGuiScrollbar scrollBar) {
		this.scrollBar = scrollBar;
	}

	protected List<InternalSlotME> getMeSlots() {
		return meSlots;
	}

	public static final synchronized boolean isSwitchingGuis() {
		return switchingGuis;
	}

	public static final synchronized void setSwitchingGuis(final boolean switchingGuisIn) {
		switchingGuis = switchingGuisIn;
	}

	public static boolean isTabKeyDown() {
		if (Minecraft.IS_RUNNING_ON_MAC) {
			return Keyboard.isKeyDown(48);
		}
		else {
			return Keyboard.isKeyDown(15);
		}
	}

	public static class Size1Slot extends SlotItemHandler {

		private final SlotItemHandler delegate;

		public Size1Slot(SlotItemHandler delegate) {
			super(delegate.getItemHandler(), delegate.getSlotIndex(), delegate.xPos, delegate.yPos);
			this.delegate = delegate;
		}

		/**
		 * Helper fnct to get the stack in the slot.
		 */
		@Override
		@Nonnull
		public ItemStack getStack() {
			ItemStack orgStack = delegate.getStack();
			if (!orgStack.isEmpty()) {
				ItemStack modifiedStack = orgStack.copy();
				modifiedStack.setCount(1);
				return modifiedStack;
			}

			return ItemStack.EMPTY;
		}

		/**
		 * Returns if this slot contains a stack.
		 */
		@Override
		public boolean getHasStack() {
			return delegate.getHasStack();
		}

		/**
		 * returns true if the slot exists in the given inventory and location
		 */
		@Override
		public boolean isHere(IInventory inv, int slotIn) {
			return delegate.isHere(inv, slotIn);
		}

		/**
		 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case of
		 * armor slots)
		 */
		@Override
		public int getSlotStackLimit() {
			return delegate.getSlotStackLimit();
		}

		@Override
		public int getItemStackLimit(ItemStack stack) {
			return delegate.getItemStackLimit(stack);
		}

		@Override
		@Nullable
		@SideOnly(Side.CLIENT)
		public String getSlotTexture() {
			return delegate.getSlotTexture();
		}

		/**
		 * Return whether this slot's stack can be taken from this slot.
		 */
		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			return delegate.canTakeStack(playerIn);
		}

		/**
		 * Actualy only call when we want to render the white square effect over the slots. Return always True, except for the
		 * armor slot of the Donkey/Mule (we can't interact with the Undead and Skeleton horses)
		 */
		@Override
		@SideOnly(Side.CLIENT)
		public boolean isEnabled() {
			return delegate.isEnabled();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ResourceLocation getBackgroundLocation() {
			return delegate.getBackgroundLocation();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public TextureAtlasSprite getBackgroundSprite() {
			return delegate.getBackgroundSprite();
		}

		@Override
		public int getSlotIndex() {
			return delegate.getSlotIndex();
		}

		@Override
		public boolean isSameInventory(Slot other) {
			return delegate.isSameInventory(other);
		}
	}

}
