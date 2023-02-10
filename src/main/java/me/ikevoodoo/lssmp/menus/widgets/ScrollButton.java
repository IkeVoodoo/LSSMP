package me.ikevoodoo.lssmp.menus.widgets;

import me.ikevoodoo.smpcore.menus.v2.Page;
import me.ikevoodoo.smpcore.menus.v2.widgets.Widget;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ScrollButton extends Widget {

    private final int x;
    private final int y;
    private final boolean isNext;

    public ScrollButton(Page page, int x, int y, boolean isNext) {
        super(page);

        this.x = x;
        this.y = y;

        this.isNext = isNext;
    }

    @Override
    public boolean canDisplay() {
        return this.isNext ? !this.getPage().isFirst() : !this.getPage().isLast();
    }

    @Override
    public void draw(Inventory inventory, Player player) {
        inventory.setItem(x + y * 9, new ItemStack(Material.GREEN_STAINED_GLASS_PANE));
    }

    @Override
    protected void onClick(Player player) {
//        this.getPage().getMenu().show(player, this.isNext ? MenuMovement.NEXT : MenuMovement.LAST);
    }

}
