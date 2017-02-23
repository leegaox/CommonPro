package com.biu.modulebase.common.widget.expandablelistview;
/***
 * 分类接口，根据V value返回K key
 *
 * @param <K>
 * @param <V>
 */
public interface KeySort<K, V> {
    public K getKey(V v);
}
