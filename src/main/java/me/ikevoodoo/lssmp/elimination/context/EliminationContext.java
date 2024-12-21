package me.ikevoodoo.lssmp.elimination.context;

import me.ikevoodoo.lssmp.elimination.EliminationInfo;

import java.util.Objects;

public class EliminationContext {

    private final EliminationInfo info;
    private SwitchMode switchMode;

    public EliminationContext(EliminationInfo info) {
        this.info = info;

        this.resetSwitch();
    }

    public void switchTo(String switchModeId) {
        if (Objects.equals(switchModeId, this.info.configuration().eliminationMode())) {
            throw new IllegalArgumentException("An elimination mode may not switch to itself!");
        }

        this.switchMode = new SwitchMode(SwitchMode.Type.SWITCH_TO, switchModeId);
    }

    public void resetSwitch() {
        this.switchMode = new SwitchMode(SwitchMode.Type.NO_SWITCH, null);
    }

    public SwitchMode switchMode() {
        return switchMode;
    }

    public EliminationInfo info() {
        return info;
    }
}
