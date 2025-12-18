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
import jogo.gameobject.Inventory.Inventory;
import jogo.gameobject.Inventory.ItemStack;
import jogo.gameobject.character.Player;
import jogo.gameobject.item.Item;
import java.util.*;
import java.io.*;

public class HudAppState extends BaseAppState {

    private final Node guiNode;
    private final AssetManager assetManager;
    private PlayerAppState player;
    private BitmapText crosshair;

    private Picture healthBarBorder;
    private Picture healthBarBG;
    private Picture healthBar;
    private BitmapText healthBarText;
    private final float hbWidth = 284f;
    private final float hbHeight = 32f;

    private Picture hudLiquidColorEffect;

    private BitmapText topTimesText;
    private List<Float> topTimes = new ArrayList<>();
    private final String TIME_FILE = "times.txt";

    private BitmapText timerText;
    private float gameTime = 0.0f;
    private boolean gameRunning = true; // Para parar o tempo quando o jogo acabar

    private Picture[] inventorySlots;
    private BitmapText[] slotTexts;
    private Picture selectedSlot;
    private final float slotSize = 57f;

    public HudAppState(Node guiNode, AssetManager assetManager) {
        this.guiNode = guiNode;
        this.assetManager = assetManager;
    }

    @Override
    protected void initialize(Application app) {
        // get playerAppState
        player = getStateManager().getState(PlayerAppState.class);
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
        createHealthBar(pixelFont);
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
        System.out.println("HudAppState initialized: liquidEffects attached");
        // timer
        timerText = new BitmapText(pixelFont, false);
        timerText.setSize(pixelFont.getCharSet().getRenderedSize());
        timerText.setColor(ColorRGBA.White);
        timerText.setText("Tempo: 0");
        guiNode.attachChild(timerText);
        System.out.println("HudAppState initialized: timer score attached");
        // Inventory HUD
        createInventory(pixelFont);
        System.out.println("HudAppState initialized: inventory attached");

        topTimesText = new BitmapText(pixelFont, false);
        topTimesText.setSize(pixelFont.getCharSet().getRenderedSize() * 0.7f); // Um pouco mais pequeno
        topTimesText.setColor(ColorRGBA.Yellow);
        guiNode.attachChild(topTimesText);

        loadTimes();       // Carrega do disco
        updateTimeDisplay(); // Mostra na tela

        System.out.println("HudAppState initialized: highscores attached");
    }

    // method para condensar a criação da healthbar
    private void createHealthBar(BitmapFont font) {
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
        healthBarText = new BitmapText(font, false);
        healthBarText.setSize(font.getCharSet().getRenderedSize() * .5f);
    }

    private void createInventory(BitmapFont font) {
        Inventory playerInventory = player.getInventory();
        int slotCount = playerInventory.getCAPACITY();
        int selectedInvSlot = playerInventory.getSelectedSlot();

        inventorySlots = new Picture[slotCount];
        slotTexts = new BitmapText[slotCount];

        for (int i = 0; i < slotCount; i++) {
            inventorySlots[i] = new Picture("InvSlot:"+1);
            inventorySlots[i].setImage(assetManager, "Interface/InvHUD.png", true);
            inventorySlots[i].setWidth(slotSize); inventorySlots[i].setHeight(slotSize);
            guiNode.attachChild(inventorySlots[i]);
            slotTexts[i] = new BitmapText(font, false);
            slotTexts[i].setSize(font.getCharSet().getRenderedSize() * .35f);
            guiNode.attachChild(slotTexts[i]);
        }

        selectedSlot = new Picture("SelectedSlot");
        selectedSlot.setImage(assetManager, "Interface/InvHover.png", true);
        selectedSlot.setWidth(slotSize); selectedSlot.setHeight(slotSize);
        guiNode.attachChild(selectedSlot);
    }

    private void updateInventory(int w, int h) {
        Inventory playerInventory = player.getInventory();
        int slotCount = playerInventory.getCAPACITY();
        int selectedInvSlot = playerInventory.getSelectedSlot();

        for (int i = 0; i < slotCount; i++) {
            float tx = w/6f + ((i * inventorySlots[i].getWidth())) ;
            float ty = inventorySlots[i].getHeight()-20f;
            inventorySlots[i].setLocalTranslation(tx, ty, 0);
            slotTexts[i].setLocalTranslation(tx + (slotTexts[i].getLineWidth()/10f), ty + (slotTexts[i].getLineHeight()*4f), 1);
            ItemStack slotStack = playerInventory.getItemStack(i);
            if (slotStack != null) {
                Item stackItem = slotStack.getItem();
                slotTexts[i].setText(stackItem.getName() + "\n\n    x" + slotStack.getStack());
            } else {
                slotTexts[i].setText("");
            }
        }

        selectedSlot.setLocalTranslation(inventorySlots[selectedInvSlot].getLocalTranslation().x, inventorySlots[selectedInvSlot].getLocalTranslation().y, 2);
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

    public void centerTimer(int w, int h) {
        timerText.setLocalTranslation(w - 180, h - 20, 0);
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
    // Carrega os scores do ficheiro para a lista
    private void loadTimes() {
        topTimes.clear();
        File file = new File(TIME_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    topTimes.add(Float.parseFloat(line));
                } catch (NumberFormatException e) {
                    System.out.println("Erro ao ler score: " + line);
                }
            }
            // Ordena: tempos menores primeiro (mais rápido é melhor)
            Collections.sort(topTimes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Guarda a lista atual no ficheiro
    private void saveTimes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TIME_FILE))) {
            // Guarda apenas os top 5 para não encher o ficheiro
            int count = 0;
            for (Float score : topTimes) {
                if (count >= 5) break;
                writer.write(String.valueOf(score));
                writer.newLine();
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateTimeDisplay() {
        StringBuilder sb = new StringBuilder("TOP TEMPOS:\n");
        int count = 0;
        for (Float score : topTimes) {
            if (count >= 5) break; // Mostra só os top 5
            sb.append(String.format("%d. %.1fs\n", (count + 1), score));
            count++;
        }
        topTimesText.setText(sb.toString());
    }
    // Adiciona um novo tempo, ordena e guarda
    public void addScore(float time) {
        topTimes.add(time);
        Collections.sort(topTimes); // Menor tempo primeiro
        saveTimes();
        updateTimeDisplay(); // Atualiza o texto no ecrã
    }
    public void positionHighScores(int w, int h) {
        // Posiciona no canto superior direito, abaixo do timer (ajusta o -50 ou -100 conforme necessário)
        topTimesText.setLocalTranslation(w - 180, h - 60, 0);
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
        centerTimer(w, h);
        positionHighScores(w, h);
        updateInventory(w, h);
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
        if (this.gameRunning) { // Só guarda se o jogo estava a correr
            this.gameRunning = false;
            addScore(gameTime); // Guarda o tempo atual na tabela
            System.out.println("Jogo terminado! Tempo registado: " + gameTime);
        }
    }
    public float getFinalTime() {
        return gameTime;
    }

    @Override
    protected void cleanup(Application app) {
        if (crosshair != null) crosshair.removeFromParent();
        stopTimer();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}

