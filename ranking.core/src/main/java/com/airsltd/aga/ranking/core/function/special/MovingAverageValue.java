package com.airsltd.aga.ranking.core.function.special;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class MovingAverageValue<T> implements Iterable<T> {

	private Deque<T> f_series = new LinkedList<>();
	private int f_size;

	public MovingAverageValue(Integer p_type) {
		f_size = p_type;
	}

	public void addValue(T p_value) {
		if (f_series.size() == f_size) {
			f_series.pollFirst();
		}
		f_series.addLast(p_value);
	}

	public boolean full() {
		return f_size == f_series.size();
	}

	/**
	 * @return the series
	 */
	public Deque<T> getSeries() {
		return f_series;
	}

	/**
	 * @param p_series
	 *            the series to set
	 */
	public void setSeries(Deque<T> p_series) {
		f_series = p_series;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return f_size;
	}

	/**
	 * @param p_size
	 *            the size to set
	 */
	public void setSize(int p_size) {
		f_size = p_size;
	}

	public void put(int p_index, T p_value) {
		if (p_index < f_series.size()) {
			((LinkedList<T>) f_series).set(p_index, p_value);
		}
	}
	
	public T get(int p_index) {
		T l_retVal = null;
		if (p_index < f_series.size()) {
			l_retVal = ((LinkedList<T>) f_series).get(p_index);
		}
		return l_retVal;
	}

	@Override
	public Iterator<T> iterator() {
		return f_series.iterator();
	}

	public void clear() {
		f_series.clear();
	}

}
