package win.korowin.enhanced_attack_indicator_reborn.client.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import win.korowin.enhanced_attack_indicator_reborn.EnhancedAttackIndicatorReborn;

import java.util.function.Consumer;

public final class EAIRConfigScreen extends Screen {
	private static final String PREFIX = EnhancedAttackIndicatorReborn.MOD_ID + ".configuration.";
	private static final int FOOTER_LEFT_WIDTH = 308;
	private static final int FOOTER_BUTTON_WIDTH = 100;
	private static final int FOOTER_BUTTON_HEIGHT = 20;
	private static final int FOOTER_BUTTON_GAP = 4;
	private static final int LIST_TOP = 32;
	private static final int LIST_BOTTOM_PADDING = 32;
	private static final int LIST_ENTRY_HEIGHT = 25;
	private static final int LIST_SIDE_PADDING = 12;
	private static final int ENTRY_LABEL_LEFT_PADDING = 0;
	private static final int ENTRY_BUTTON_RIGHT_PADDING = 0;
	private static final int SCROLLBAR_WIDTH = 6;
	private static final int SCROLLBAR_RIGHT_PADDING = 2;

	private final Screen parent;
	private ConfigList list;

	public EAIRConfigScreen(Screen parent) {
		super(Component.translatable(PREFIX + "title"));
		this.parent = parent;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
		super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
		guiGraphics.centeredText(this.font, this.getTitle(), this.width / 2, 12, 0xFFFFFFFF);
	}

	@Override
	protected void init() {
		super.init();

		Config.load();

		int listHeight = this.height - LIST_BOTTOM_PADDING - LIST_TOP;
		this.list = new ConfigList(Minecraft.getInstance(), this.width, listHeight, LIST_TOP, LIST_ENTRY_HEIGHT);
		this.addRenderableWidget(this.list);

		this.list.addConfigEntry(new EnumEntry(
				Component.translatable(PREFIX + "weaponCoolDownImportance"),
				() -> Config.weaponCoolDownImportance,
				value -> Config.weaponCoolDownImportance = value
		));
		this.list.addConfigEntry(new BooleanEntry(
				Component.translatable(PREFIX + "disablePickaxesAndShovels"),
				() -> Config.disablePickaxesAndShovels,
				value -> Config.disablePickaxesAndShovels = value
		));
		this.list.addConfigEntry(new BooleanEntry(
				Component.translatable(PREFIX + "disableAxes"),
				() -> Config.disableAxes,
				value -> Config.disableAxes = value
		));
		this.list.addConfigEntry(new BooleanEntry(
				Component.translatable(PREFIX + "showBlockBreaking"),
				() -> Config.showBlockBreaking,
				value -> Config.showBlockBreaking = value
		));
		this.list.addConfigEntry(new BooleanEntry(
				Component.translatable(PREFIX + "showRangeWeaponDraw"),
				() -> Config.showRangeWeaponDraw,
				value -> Config.showRangeWeaponDraw = value
		));
		this.list.addConfigEntry(new BooleanEntry(
				Component.translatable(PREFIX + "showItemCooldowns"),
				() -> Config.showItemCooldowns,
				value -> Config.showItemCooldowns = value
		));
		this.list.addConfigEntry(new BooleanEntry(
				Component.translatable(PREFIX + "showFoodAndPotions"),
				() -> Config.showFoodAndPotions,
				value -> Config.showFoodAndPotions = value
		));
		this.list.addConfigEntry(new BooleanEntry(
				Component.translatable(PREFIX + "showSleep"),
				() -> Config.showSleep,
				value -> Config.showSleep = value
		));
		this.list.addConfigEntry(new BooleanEntry(
				Component.translatable(PREFIX + "showItemContainerFullness"),
				() -> Config.showItemContainerFullness,
				value -> Config.showItemContainerFullness = value
		));

		int footerLeft = this.width / 2 - (FOOTER_LEFT_WIDTH / 2);
		this.addRenderableWidget(Button.builder(Component.translatable(PREFIX + "reset"), button -> {
			Config.resetToDefaults();
			Config.save();
			Minecraft.getInstance().setScreenAndShow(new EAIRConfigScreen(this.parent));
		}).bounds(footerLeft, this.height - 28, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT).build());

		this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> {
			Config.load();
			Minecraft.getInstance().setScreenAndShow(this.parent);
		}).bounds(footerLeft + FOOTER_BUTTON_WIDTH + FOOTER_BUTTON_GAP, this.height - 28, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT).build());

		this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> {
			Config.save();
			Minecraft.getInstance().setScreenAndShow(this.parent);
		}).bounds(footerLeft + (FOOTER_BUTTON_WIDTH + FOOTER_BUTTON_GAP) * 2, this.height - 28, FOOTER_BUTTON_WIDTH, FOOTER_BUTTON_HEIGHT).build());
	}

	@Override
	public void onClose() {
		Config.load();
		Minecraft.getInstance().setScreenAndShow(this.parent);
	}

	private static final class ConfigList extends ObjectSelectionList<ConfigEntry> {
		private ConfigList(Minecraft minecraft, int width, int height, int top, int bottom) {
			super(minecraft, width, height, top, bottom);
			this.centerListVertically = false;
		}

		public void addConfigEntry(ConfigEntry entry) {
			this.addEntry(entry);
		}

		@Override
		public int getRowWidth() {
			return this.width - LIST_SIDE_PADDING * 2;
		}

		@Override
		protected int scrollBarX() {
			return this.getX() + this.getWidth() - SCROLLBAR_WIDTH - SCROLLBAR_RIGHT_PADDING;
		}
	}

	private abstract static class ConfigEntry extends ObjectSelectionList.Entry<ConfigEntry> {
		protected final Component label;
		protected final Button button;

		protected ConfigEntry(Component label, Button button) {
			this.label = label;
			this.button = button;
		}

		@Override
		public Component getNarration() {
			return this.label;
		}

		@Override
		public void visitWidgets(Consumer<AbstractWidget> consumer) {
			consumer.accept(this.button);
		}

		@Override
		public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
			return this.button.mouseClicked(event, bl);
		}

		@Override
		public boolean mouseReleased(MouseButtonEvent event) {
			return this.button.mouseReleased(event);
		}

		@Override
		public boolean mouseDragged(MouseButtonEvent event, double d, double e) {
			return this.button.mouseDragged(event, d, e);
		}

		@Override
		public void extractContent(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float delta) {
			int contentX = this.getContentX();
			int contentY = this.getContentY();
			int contentRight = this.getContentRight();

			this.button.setX(contentRight - this.button.getWidth() - ENTRY_BUTTON_RIGHT_PADDING);
			this.button.setY(contentY);
			this.button.extractRenderState(guiGraphics, mouseX, mouseY, delta);
			guiGraphics.text(Minecraft.getInstance().font, this.label, contentX + ENTRY_LABEL_LEFT_PADDING, contentY + 6, 0xFFFFFFFF);
		}
	}

	private interface BoolGetter {
		boolean get();
	}

	private interface BoolSetter {
		void set(boolean value);
	}

	private static final class BooleanEntry extends ConfigEntry {
		private BooleanEntry(Component label, BoolGetter getter, BoolSetter setter) {
			super(label, Button.builder(createLabel(getter.get()), button -> {
				boolean next = !getter.get();
				setter.set(next);
				button.setMessage(createLabel(next));
			}).bounds(0, 0, 100, 20).build());
		}

		private static Component createLabel(boolean value) {
			return Component.literal(value ? "True" : "False").withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED);
		}
	}

	private interface EnumGetter {
		Config.WeaponCoolDownImportance get();
	}

	private interface EnumSetter {
		void set(Config.WeaponCoolDownImportance value);
	}

	private static final class EnumEntry extends ConfigEntry {
		private EnumEntry(Component label, EnumGetter getter, EnumSetter setter) {
			super(label, Button.builder(getEnumLabel(getter.get()), button -> {
				Config.WeaponCoolDownImportance current = getter.get();
				Config.WeaponCoolDownImportance[] values = Config.WeaponCoolDownImportance.values();
				int index = current.ordinal() + 1;
				Config.WeaponCoolDownImportance next = values[index >= values.length ? 0 : index];
				setter.set(next);
				button.setMessage(getEnumLabel(next));
			}).bounds(0, 0, 100, 20).build());
		}

		private static Component getEnumLabel(Config.WeaponCoolDownImportance value) {
			return Component.translatable(PREFIX + "enum.WeaponCoolDownImportance." + value.name());
		}
	}
}
