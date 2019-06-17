# AE2 Wireless Terminal Library

This is a library and API which allows for easy addition of Infinite range-capable Wireless Terminals for AE2 with built-in Baubles support.

The current API implementation is kinda bloated for a full implementation which includes Infinity Booster support, and I do plan to make things simpler in time.

Several things have to fall into place in order for your terminal to function properly and be compatible with WUT (Wireless Universal Terminal), so I'll break this up into sections labeled with their respective classes.


#API
##Item
* Your item should implement a custom interface which extends `p455w0rd.ae2wtlib.api.ICustomWirelessTerminalItem`

I recommend overriding `ICustomWirelessTerminalItem#getStorageChannel` using a default implementation inside this custom interface. [Example](https://github.com/p455w0rd/WirelessCraftingTerminal/blob/f5cb620eaf755f4ffaf32b4279b30accd3a8ce3e/src/main/java/p455w0rd/wct/api/IWirelessCraftingTerminalItem.java#L32)

* **`ICustomWirelessTerminalItem`** also extends **`p455w0rd.ae2wtlib.api.client.IBaubleItem`** which adds the **`getRender()Lp455w0rd.ae2wtlib.api.client;`** method for custom rendering per slot

* Your item should also extend **`p455w0rd.ae2wtlib.api.item.ItemWT`** which is an abstract implementation of a general wireless terminal which adds support for internal rendering.
Check [this](https://github.com/p455w0rd/WirelessCraftingTerminal/blob/f5cb620eaf755f4ffaf32b4279b30accd3a8ce3e/src/main/java/p455w0rd/wct/items/ItemWCT.java) for an example of what should be overridden

##Container
* Your container should extend **`p455w0rd.ae2wtlib.api.container.ContainerWT`**. In the constructor of your container, you will need to call **`ContainerWT#setTerminalHost`**. [Example](https://github.com/p455w0rd/WirelessCraftingTerminal/blob/f5cb620eaf755f4ffaf32b4279b30accd3a8ce3e/src/main/java/p455w0rd/wct/container/ContainerWCT.java#L104)

* If you wish to make use of the **Infinity Booster Card** functionality, you should call the **`ContainerWT#<init>(Lnet.minecraft.entity.player.InventoryPlayer;Ljava.lang.Object;ISSII)V`** super constructor, where the final 3 parameters should be `true (enable the booster slot), slotXPos, slotYPos`.

* If your terminal will be making use of a ConfigManager (For controlling of GUI settings), you should also initialize that in the constructor. [Example](https://github.com/p455w0rd/WirelessCraftingTerminal/blob/f5cb620eaf755f4ffaf32b4279b30accd3a8ce3e/src/main/java/p455w0rd/wct/container/ContainerWCT.java#L105)

* I have included a trash slot for convenience. When adding slots, call **`ContainerWT#addSlotToContainer(WTApi.instance().createTrashSlot(trashInventory, xPos, yPos));`**

##GUI
* Your GUI should extend **`p455w0rd.ae2wtlib.api.client.gui.GuiWT`** to ensure Compatibility with the **Wireless Universal Terminal**