package me.ikevoodoo.lssmp.pipeline;

public enum PipelineResult {

    CANCEL,
    CONTINUE,
    SKIP_NEXT;

    public static PipelineResult fromBoolean(boolean b) {
        return b ? CONTINUE : CANCEL;
    }

}
