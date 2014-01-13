package com.gserver.utils;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Lists;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * 一致性哈希
 * 
 * @author Administrator
 * 
 * @param <T>
 */
public class ConsistentHash<T> {

	private final HashFunction hashFunction;
	private final int numberOfReplicas;// 虚拟节点数目
	private final SortedMap<Integer, T> circle = new TreeMap<Integer, T>();

	public ConsistentHash(HashFunction hashFunction, int numberOfReplicas,
			Collection<T> nodes) {

		this.hashFunction = hashFunction;
		this.numberOfReplicas = numberOfReplicas;

		for (T node : nodes) {
			add(node);
		}
	}

	public void add(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.put(hashFunction.hashUnencodedChars(node.toString() + i).hashCode(),
					node);
		}
	}

	public void remove(T node) {
		for (int i = 0; i < numberOfReplicas; i++) {
			circle.remove(hashFunction.hashUnencodedChars(node.toString() + i)
					.hashCode());
		}
	}

	public T get(Object key) {
		if (circle.isEmpty()) {
			return null;
		}
		int hash = hashFunction.hashUnencodedChars(key.toString()).hashCode();
		SortedMap<Integer, T> tailMap = circle.tailMap(hash);
		hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
		return circle.get(hash);
	}

	/**
	 * 上面的代码用一个integer的sorted map来表示hash
	 * circle。当ConsistentHash创建时，每个node都被添加到circle
	 * map中(添加的次数由numberOfReplicas控制)。每个replica的位置，由node的名字加上一个数字后缀所对应的hash值来决定。
	 * 要为一个object找到它应该去的node
	 * (get方法)，我们把object的hash值放入map中查找。大多数情况下，不会恰好有一个node和这个object重合
	 * (即使每个node都有一定量的replica
	 * ，hash的值空间也比node数要多得多)，所以用tailMap方法找到map中的下一个key。如果tail
	 * map为空，那么我们转一圈，找到circle中的第一个key。
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<Integer> node = Lists.newArrayList();
		node.add(1);
		node.add(2);
		ConsistentHash<Integer> hash = new ConsistentHash<Integer>(
				Hashing.md5(), 64, node);

		hash.add(3);// add node
		hash.remove(2);// remove node

		int a = 0;
		int b = 0;
		for (int i = 0; i < 100; i++) {
			Integer integer = hash.get(i);// value hash
			if (integer == 1) {
				a++;
			} else {
				b++;
			}
		}

		System.out.println("1 cnt=" + a + "     " + "2 cnt=" + b);
	}
}
