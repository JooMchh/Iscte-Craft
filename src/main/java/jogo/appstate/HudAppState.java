package jogo.appstate;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.ui.Picture;

import java.awt.*;

public class HudAppState extends BaseAppState {

    private final Node guiNode;
    private final AssetManager assetManager;
    private BitmapText crosshair;
    private Picture healthBarBorder;
    private Picture healthBarBG;
    private Picture healthBar;
    private BitmapText healthBarText;
    private Picture hudLiquidColorEffect;
    private float hbWidth = 284f;
    private float hbHeight = 32f;
    private BitmapText timerText;
    private float gameTime = 0.0f;
    private boolean gameRunning = true; // Para parar o tempo quando o jogo acabar

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
        System.out.println("HudAppState initialized: crosshair attached");
        // Health bar stuff
        healthBarBG = new Picture("Health Bar Background");
        healthBarBG.setImage(assetManager, "Interface/healthbarborderBG.png", true);
        healthBarBG.setWidth(hbWidth);
        healthBarBG.setHeight(hbHeight);
        healthBar = new Picture("Health Bar");
        healthBar.setImage(assetManager, "Interface/healthbar.png", true);
        healthBar.setWidth(hbWidth);
        healthBar.setHeight(hbHeight);
        healthBarBorder = new Picture("Health Bar Border");
        healthBarBorder.setImage(assetManager, "Interface/healthbarborder.png", true);
        healthBarBorder.setWidth(hbWidth);
        healthBarBorder.setHeight(hbHeight);
        healthBarText = new BitmapText(pixelFont, false);
        healthBarText.setSize(font.getCharSet().getRenderedSize());
        guiNode.attachChild(healthBarBG);
        guiNode.attachChild(healthBar);
        guiNode.attachChild(healthBarBorder);
        guiNode.attachChild(healthBarText);
        updateHealthBar(100, 100);
        System.out.println("HudAppState initialized: health bar attached");
        // Screen effects
        hudLiquidColorEffect = new Picture(";LiquidHudEffect");
        hudLiquidColorEffect.setImage(assetManager, "Interface/Colorhudeffect.png", true);
        hudLiquidColorEffect.setCullHint(Spatial.CullHint.Always);
        guiNode.attachChild(hudLiquidColorEffect);
        // timer
        BitmapFont myFont = app.getAssetManager().loadFont("Interface/Fonts/AlagardFNT.fnt");

        timerText = new BitmapText(myFont, false);
        timerText.setSize(myFont.getCharSet().getRenderedSize());
        timerText.setColor(ColorRGBA.White);
        timerText.setText("Tempo: 0");

        // Posicionar no topo direito (exemplo)
        SimpleApplication simpleApp = (SimpleApplication) app;
        float width = simpleApp.getCamera().getWidth();
        float height = simpleApp.getCamera().getHeight();
        timerText.setLocalTranslation(width - 180, height - 20, 0); // Ajusta a posição

        simpleApp.getGuiNode().attachChild(timerText);
    }

    private void centerCrosshair(int w, int h) {
        float x = (w - crosshair.getLineWidth()) / 2f;
        float y = (h + crosshair.getLineHeight()) / 2f;
        crosshair.setLocalTranslation(x, y, 0);
    }

    private void centerHealthBar(int h) {
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

    public void centerHudEffects(int w, int h) {
        float x = (w / 2f);
        float y = (h / 2f);
        crosshair.setLocalTranslation(x, y, 0);
        hudLiquidColorEffect.setWidth(w);
        hudLiquidColorEffect.setHeight(h);
    }

    public void updateHealthBar(float health, float maxHealth) {
        float barRatio = (health / maxHealth);
        healthBar.setWidth(hbWidth*barRatio);
        healthBarText.setText((int) health + " / "+ (int) maxHealth);
        System.out.println("Health bar updated!");
    }

    public void updateLiquidEffect(byte liquid, boolean inLiquid) { // method with case para mudar e mostrar hud effect under liquid
        if (inLiquid) {
            switch (liquid) {
                case 7:
                    hudLiquidColorEffect.getMaterial().setColor("Color", new ColorRGBA(0.3f, 0.04f, 0.5f, .55f));
                    break;
                case 8:
                    hudLiquidColorEffect.getMaterial().setColor("Color", new ColorRGBA(1f, 0.13f, 0f, .55f));
                    break;
                case 9:
                    hudLiquidColorEffect.getMaterial().setColor("Color", new ColorRGBA(0f, 0.07f, .5f, .35f));
                    break;
            }
            hudLiquidColorEffect.setCullHint(Spatial.CullHint.Never);
        } else {
            hudLiquidColorEffect.setCullHint(Spatial.CullHint.Always);
        }
    }

    @Override
    public void update(float tpf) {
        // keep centered (cheap)
        SimpleApplication sapp = (SimpleApplication) getApplication();
        int w = sapp.getCamera().getWidth();
        int h = sapp.getCamera().getHeight();
        centerCrosshair(w, h);
        centerHealthBar(h);
        centerHudEffects(w, h);
        // tpf = Time Per Frame (tempo que passou desde o último frame em segundos)

        if (gameRunning) {
            // 2. Incrementar o tempo
            gameTime += tpf;

            // 3. Atualizar o texto na tela
            // (int) gameTime remove as casas decimais para ficar mais limpo
            timerText.setText("Tempo: " + (int) gameTime + "s");
        }
    }
    public void stopTimer() {
        this.gameRunning = false;
    }
    public float getFinalTime() {
        return gameTime;
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

