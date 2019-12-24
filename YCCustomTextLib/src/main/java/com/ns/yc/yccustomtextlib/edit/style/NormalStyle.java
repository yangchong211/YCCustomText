package com.ns.yc.yccustomtextlib.edit.style;

import android.text.Editable;
import android.text.Spanned;
import android.util.Pair;

import java.lang.reflect.ParameterizedType;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/12/24
 *     desc  : 样式抽象类
 *     revise:
 * </pre>
 */
public abstract class NormalStyle<E> {

    private Class<E> clazzE;

    public NormalStyle() {
        clazzE = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 样式情况判断
     * @param editable                      editable
     * @param start                         start
     * @param end                           end
     */
    public void applyStyle(Editable editable, int start, int end) {
        //获取 从  start 到 end 位置上所有的指定 class 类型的 Span数组
        E[] spans = editable.getSpans(start, end, clazzE);
        E existingSpan = null;
        if (spans.length > 0) {
            existingSpan = spans[0];
        }
        if (existingSpan == null) {
            //当前选中内部无此样式，开始设置span样式
            checkAndMergeSpan(editable, start, end, clazzE);
        } else {
            //获取 一个 span 的起始位置
            int existingSpanStart = editable.getSpanStart(existingSpan);
            //获取一个span 的结束位置
            int existingSpanEnd = editable.getSpanEnd(existingSpan);
            if (existingSpanStart <= start && existingSpanEnd >= end) {
                //在一个 完整的 span 中
                //删除 样式
                removeStyle(editable, start, end, clazzE, true);
            } else {
                //当前选中区域存在了某某样式，需要合并样式
                checkAndMergeSpan(editable, start, end, clazzE);
            }
        }
    }

    /**
     *
     *
     *
     * setSpan(Object what, int start, int end, int flags); flag的四种类型
     * Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)；
     * Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)；
     * Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)；
     * Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)。
     * @param editable                              editable
     * @param start                                 start
     * @param end                                   end
     * @param clazzE                                clazz
     * @param isSame                                是否在 同一个 span 内部
     */
    private void removeStyle(Editable editable, int start, int end, Class<E> clazzE, boolean isSame) {

        E[] spans = editable.getSpans(start, end, clazzE);
        if (spans.length > 0) {
            if (isSame) {
                //在 同一个 span 中
                E span = spans[0];
                if (null != span) {
                    //
                    // User stops the style, and wants to show
                    // un-UNDERLINE characters
                    int ess = editable.getSpanStart(span);
                    int ese = editable.getSpanEnd(span);
                    if (start >= ese) {
                        // User inputs to the end of the existing e span
                        // End existing e span
                        editable.removeSpan(span);
                        //设置 span  这里的 what 指的是 span 对象
                        //从 start 到 end 位置 设置 span 样式
                        //flags 为 Spanned中的变量
                        editable.setSpan(span, ess, start - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (start == ess && end == ese) {
                        // Case 1 desc:
                        // *BBBBBB*
                        // All selected, and un-showTodo e
                        editable.removeSpan(span);
                    } else if (start > ess && end < ese) {
                        // Case 2 desc:
                        // BB*BB*BB
                        // *BB* is selected, and un-showTodo e
                        editable.removeSpan(span);
                        E spanLeft = newSpan();
                        editable.setSpan(spanLeft, ess, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        E spanRight = newSpan();
                        editable.setSpan(spanRight, end, ese, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (start == ess && end < ese) {
                        // Case 3 desc:
                        // *BBBB*BB
                        // *BBBB* is selected, and un-showTodo e
                        editable.removeSpan(span);
                        E newSpan = newSpan();
                        editable.setSpan(newSpan, end, ese, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } else if (start > ess && end == ese) {
                        // Case 4 desc:
                        // BB*BBBB*
                        // *BBBB* is selected, and un-showTodo e
                        editable.removeSpan(span);
                        E newSpan = newSpan();
                        editable.setSpan(newSpan, ess, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } else {
                Pair<E, E> firstAndLast = findFirstAndLast(editable, spans);

                E firstSpan = firstAndLast.first;
                E lastSpan = firstAndLast.second;

                int leftStart = editable.getSpanStart(firstSpan);

                int rightEnd = editable.getSpanEnd(lastSpan);

                editable.removeSpan(firstSpan);
                editable.setSpan(firstSpan, leftStart, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                editable.removeSpan(lastSpan);
                editable.setSpan(lastSpan,end,rightEnd,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }


    }

    public Pair<E, E> findFirstAndLast(Editable editable, E[] targetSpans) {
        if (targetSpans!=null && targetSpans.length > 0) {
            E firstTargetSpan = targetSpans[0];
            E lastTargetSpan = targetSpans[0];
            int firstTargetSpanStart = editable.getSpanStart(firstTargetSpan);
            int lastTargetSpanEnd = editable.getSpanEnd(firstTargetSpan);
            for (E lns : targetSpans) {
                int lnsStart = editable.getSpanStart(lns);
                int lnsEnd = editable.getSpanEnd(lns);
                if (lnsStart < firstTargetSpanStart) {
                    firstTargetSpan = lns;
                    firstTargetSpanStart = lnsStart;
                }
                if (lnsEnd > lastTargetSpanEnd) {
                    lastTargetSpan = lns;
                    lastTargetSpanEnd = lnsEnd;
                }
            }
            return new Pair(firstTargetSpan, lastTargetSpan);
        }
        return null;
    }


    /**
     * 边界判断与设置
     * @param editable                                  editable
     * @param start                                     start选择起始位置
     * @param end                                       end选择末尾位置
     * @param clazzE                                    clazz
     */
    private void checkAndMergeSpan(Editable editable, int start, int end, Class<E> clazzE) {
        E leftSpan = null;
        E[] leftSpans = editable.getSpans(start, start, clazzE);
        if (leftSpans.length > 0) {
            leftSpan = leftSpans[0];
        }

        E rightSpan = null;
        E[] rightSpans = editable.getSpans(end, end, clazzE);
        if (rightSpans.length > 0) {
            rightSpan = rightSpans[0];
        }

        //获取 两侧的 起始与 结束位置
        int leftSpanStart = editable.getSpanStart(leftSpan);
        int leftSpanEnd = editable.getSpanEnd(leftSpan);
        int rightStart = editable.getSpanStart(rightSpan);
        int rightSpanEnd = editable.getSpanEnd(rightSpan);

        removeAllSpans(editable, start, end, clazzE);
        if (leftSpan != null && rightSpan != null) {
            if (leftSpanEnd == rightStart) {
                //选中的两端是  连续的 样式
                removeStyle(editable, start, end, clazzE, false);
            } else {
                E eSpan = newSpan();
                editable.setSpan(eSpan, leftSpanStart, rightSpanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (leftSpan != null && rightSpan == null) {
            E eSpan = newSpan();
            editable.setSpan(eSpan, leftSpanStart, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (leftSpan == null && rightSpan != null) {
            E eSpan = newSpan();
            editable.setSpan(eSpan, start, rightSpanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            E eSpan = newSpan();
            editable.setSpan(eSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    protected abstract E newSpan();


    private <E> void removeAllSpans(Editable editable, int start, int end, Class<E> clazzE) {
        E[] allSpans = editable.getSpans(start, end, clazzE);
        for (E span : allSpans) {
            editable.removeSpan(span);
        }
    }
}
