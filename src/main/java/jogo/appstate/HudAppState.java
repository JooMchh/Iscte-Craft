package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

public class HudAppState extends BaseAppState {

    private final Node guiNode;
    private final AssetManager assetManager;
    private BitmapText crosshair;
    private Picture healthBarBorder;
    private Picture healthBarBG;
    private Picture healthBar;
    private BitmapText healthBarText;
    private float hbWidth = 284f;
    private float hbHeight = 32f;

    public HudAppState(Node guiNode, AssetManager assetManager) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
    }

    @Override
    protected void initialize(Application app) {
        // font
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapFont pixelFont = assetManager.loadFont("Interface/Fonts/AlagardFNT.fnt");
        // crossHair
        crosshair = new BitmapText(font, false);
        crosshair.setText("+");
        crosshair.setSize(font.getCharSet().getRenderedSize() * 2f);
        guiNode.attachChild(crosshair);
        centerCrosshair();
        System.out.println("HudAppState initialized: crosshair attached");
        // Health bar stuff
        healthBarBG = new Picture("Health Bar Background");
        healthBarBG.setImage(assetManager, "Textures/healthbarborderBG.png", true);
        healthBarBG.setWidth(hbWidth);
        healthBarBG.setHeight(hbHeight);
        healthBar = new Picture("Health Bar");
        healthBar.setImage(assetManager, "Textures/healthbar.png", true);
        healthBar.setWidth(hbWidth);
        healthBar.setHeight(hbHeight);
        healthBarBorder = new Picture("Health Bar Border");
        healthBarBorder.setImage(assetManager, "Textures/healthbarborder.png", true);
        healthBarBorder.setWidth(hbWidth);
        healthBarBorder.setHeight(hbHeight);
        healthBarText = new BitmapText(pixelFont, false);
        healthBarText.setSize(font.getCharSet().getRenderedSize());
        guiNode.attachChild(healthBarBG);
        guiNode.attachChild(healthBar);
        guiNode.attachChild(healthBarBorder);
        guiNode.attachChild(healthBarText);
        centerHealthBar();
        updateHealthBar(100, 100);
        System.out.println("HudAppState initialized: health bar attached");
    }

    private void centerCrosshair() {
        SimpleApplication sapp = (SimpleApplication) getApplication();
        int w = sapp.getCamera().getWidth();
        int h = sapp.getCamera().getHeight();
        float x = (w - crosshair.getLineWidth()) / 2f;
        float y = (h + crosshair.getLineHeight()) / 2f;
        crosshair.setLocalTranslation(x, y, 0);
    }

    private void centerHealthBar() {
        SimpleApplication sapp = (SimpleApplication) getApplication();
        int w = sapp.getCamera().getWidth();
        int h = sapp.getCamera().getHeight();
        // Background
        float x = 20f;
        float y = h - healthBarBG.getHeight() - 20f;
        healthBarBG.setLocalTranslation(x, y, 0);
        // Border
        healthBarBorder.setLocalTranslation(x, y, 2);
        // The Bar
        healthBar.setLocalTranslation(x, y, 1);
        // health text
        float barXCenter = x + (healthBarBG.getWidth() / 2f);
        float barYCenter = y + (healthBarBG.getHeight() / 2f);
        float textX = barXCenter - (healthBarText.getLineWidth() / 2f);
        float textY = barYCenter + (healthBarText.getLineHeight() / 2f);
        healthBarText.setLocalTranslation(textX, textY, 3);
    }

    public void updateHealthBar(float health, float maxHealth) {
        float barRatio = (health / maxHealth);
        healthBar.setWidth(hbWidth*barRatio);
        healthBarText.setText((int) health + " / "+ (int) maxHealth);
        System.out.println("Health bar updated!");
    }

    @Override
    public void update(float tpf) {
        // keep centered (cheap)
        centerCrosshair();
        centerHealthBar();
    }

    @Override
    protected void cleanup(Application app) {
        if (crosshair != null) crosshair.removeFromParent();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}

