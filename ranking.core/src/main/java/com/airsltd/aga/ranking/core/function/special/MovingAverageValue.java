package com.airsltd.aga.ranking.core.function.special;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Maintain a store of the last n values in a series.
 * <p>
 * This accumulator stores N objects.
 * The size is defined at instantiation.
 * <p>
 * When a new object T is added {@link #addValue(Object)}, the first value is dropped if the accumulator is full.
 * 
 * @author Jon Boley
 *
 * @param <T>  the Object to be stored.
 */
public class MovingAverageValue<T> implements Iterable<T> {

	/**
	 * The core of the accumulator consists of a {@link Deque} which allows for modifications on both sides.
	 */
	private Deque<T> f_series = new LinkedList<>();
	/**
	 * Size of the accumulator.
	 * <p>
	 * Defined at instantiation.
	 */
	private int f_size;

	/**
	 * Constructor.
	 * 
	 * @param p_type  not null, an number of values to store in the accumulator.
	 */
	public MovingAverageValue(Integer p_type) {
		f_size = p_type;
	}

	/**
	 * Add a value to the accumulator.
	 * <p>
	 * If the accumulator is full, the first value is dropped.
	 * @param p_value  T, the value to add
	 */
	public void addValue(T p_value) {
		if (f_series.size() == f_size) {
			f_series.pollFirst();
		}
		f_series.addLast(p_value);
	}

	/**
	 * Test if the accumulator is full.
	 * 
	 * @return true if the accumulator has no more room.
	 */
	public boolean full() {
		return f_size == f_series.size();
	}

	/**
	 * Get the data.
	 * 
	 * @return the series
	 */
	public Deque<T> getSeries() {
		return f_series;
	}

	/**
	 * Load the data.
	 * <p>
	 * Primarily of value for debugging and testing.
	 * 
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

	/**
	 * Place a value in the accumulator at position p_index.
	 * <p>
	 * If the accumulator is currently smaller than p_index, then nothing happens.
	 * 
	 * @param p_index  int, the index to modify.
	 * @param p_value  T, value to set.
	 */
	public void put(int p_index, T p_value) {
		if (p_index < f_series.size()) {
			((LinkedList<T>) f_series).set(p_index, p_value);
		}
	}
	
	/**
	 * Get the value at index p_index.
	 * <p>
	 * If p_index is larger than the size of the accumulater, null will be returned.
	 * 
	 * @param p_index  int, the index to lookup.
	 * @return the T value at index p_index.
	 */
	public T get(int p_index) {
		T l_retVal = null;
		if (p_index < f_series.size()) {
			l_retVal = ((LinkedList<T>) f_series).get(p_index);
		}
		return l_retVal;
	}

	/**
	 * Return an iterator of the data.
	 */
	@Override
	public Iterator<T> iterator() {
		return f_series.iterator();
	}

	/**
	 * Clear the accumulator.
	 */
	public void clear() {
		f_series.clear();
	}

}
