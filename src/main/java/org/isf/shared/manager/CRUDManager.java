package org.isf.shared.manager;

import java.util.List;

import org.isf.utils.exception.OHServiceException;

public interface CRUDManager<T, KeyType> {
	public boolean create(T instance) throws OHServiceException;
	public boolean update(T instance) throws OHServiceException;
	public List<T> getPage(int page, int size) throws OHServiceException;
	public T get(KeyType key) throws OHServiceException;
	public boolean delete(T instance) throws OHServiceException;
}
