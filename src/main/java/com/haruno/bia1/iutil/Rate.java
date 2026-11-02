package com.haruno.bia1.iutil;


/**
 * 表示输入已经过时间(t)或继上次时间偏移量(dt)
 * 以及全流程总时间(值域)，返回0~1的比值
 * 这是一个功能接口，通过get调用
 */
@FunctionalInterface
public interface Rate {
    /**
    * 通过进度或偏移量获取0~1之间的处理值
    * @param elapsed 从开始到现在的时间-s
    * @param delta 继上次时间的偏移量-s
    * @param span 流程总时间-s
    * @return 返回0~1之间的进度
    */
    float get(float elapsed, float delta, float span);

    /** 所有缓动都要先过这一步：把 elapsed/span 夹到 0~1 */
    static float progress(float elapsed, float span) {
        float t = elapsed / span;
        return t < 0f ? 0f : (Math.min(t, 1f));
    }


    // ---------- 线性 ----------
    Rate linearity = (e, d, s) -> progress(e, s);

    // ---------- 常用的 0→1 进度曲线 ----------
    Rate easeInQuad   = (e, d, s) -> { float x = progress(e, s); return x * x; };
    Rate easeOutQuad  = (e, d, s) -> { float x = progress(e, s); return 1f - (1f - x) * (1f - x); };
    Rate easeInOutQuad = (e, d, s) -> {
        float x = progress(e, s);
        return x < 0.5f ? 2f * x * x : 1f - 2f * (1f - x) * (1f - x);
    };
    Rate easeOutCubic = (e, d, s) -> { float x = progress(e, s); return 1f - (float) Math.pow(1 - x, 3); };
    Rate easeInOutCubic = (e, d, s) -> {
        float x = progress(e, s);
        return x < 0.5f ? 4f * x * x * x : 1f - (float) Math.pow(-2 * x + 2, 3) / 2f;
    };
    Rate easeOutQuint = (e, d, s) -> { float x = progress(e, s); return 1f - (float) Math.pow(1 - x, 5); };

    // ---------- 平滑插值（观感最“自然”，常用于镜头/位置） ----------
    Rate smoothStep   = (e, d, s) -> { float x = progress(e, s); return x * x * (3f - 2f * x); };
    Rate smootherStep = (e, d, s) -> { float x = progress(e, s); return x * x * x * (x * (x * 6f - 15f) + 10f); };

    // ---------- 会过冲（返回值 >1）：适合“弹一下” ----------
    Rate easeOutBack = (e, d, s) -> {
        final float c1 = 1.70158f, c3 = c1 + 1f;
        float x = progress(e, s);
        return 1f + c3 * (float) Math.pow(x - 1, 3) + c1 * (float) Math.pow(x - 1, 2);
    };
    Rate easeOutElastic = (e, d, s) -> {
        final float c4 = (float) (2 * Math.PI / 3);
        float x = progress(e, s);
        if (x == 0f) return 0f;
        if (x == 1f) return 1f;
        return (float) Math.pow(2, -10 * x) * (float) Math.sin((x * 10 - 0.75f) * c4) + 1f;
    };

    // ---------- 0→1→0 的“强度/幅度”曲线（不是进度！） ----------
    Rate paraCurve  = (e, d, s) -> { float x = progress(e, s); return -4f * x * (x - 1f); };
    Rate stayStatic = (e, d, s) -> 0f;
}
