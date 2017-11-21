package kr.dja.plciot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiValueMap<K, V>
{
	private final Map<K, List<V>> kvmap;
	private final Map<V, List<K>> vkmap;
	
	public MultiValueMap()
	{
		this.kvmap = new HashMap<K, List<V>>();
		this.vkmap = new HashMap<V, List<K>>();
	}
	
	public void put(K key, V value)
	{
		List<V> vlist = this.kvmap.getOrDefault(key, null);
		if(vlist == null)
		{
			vlist = new ArrayList<V>();
			this.kvmap.put(key, vlist);
		}
		vlist.add(value);
		
		List<K> klist = this.vkmap.getOrDefault(value, null);
		if(klist == null)
		{
			klist = new ArrayList<K>();
			this.vkmap.put(value, klist);
		}
		klist.add(key);
	}
	
	public List<V> get(K key)
	{
		return this.kvmap.get(key);
	}
	
	public void remove(K key, V value)
	{
		List<V> vlist = this.kvmap.getOrDefault(key, null);
		if(vlist == null) return;
		
		vlist.remove(value);
		if(vlist.size() == 0) this.kvmap.remove(key);
		
		List<K> klist = this.vkmap.get(value);
		klist.remove(key);
		if(klist.size() == 0) this.vkmap.remove(value);
	}
	
	public void removeValue(V value)
	{
		List<K> klist = this.vkmap.getOrDefault(value, null);
		if(klist == null) return;
		
		for(K key : klist)
		{
			List<V> vlist = this.kvmap.get(key);
			vlist.remove(value);
			if(vlist.size() == 0) this.kvmap.remove(key);
		}
		
		this.vkmap.remove(value);
	}
	
	public void removeKey(K key)
	{
		List<V> vlist = this.kvmap.getOrDefault(key, null);
		if(vlist == null) return;
		
		for(V value : vlist)
		{
			List<K> klist = this.vkmap.get(value);
			klist.remove(key);
			if(klist.size() == 0) this.vkmap.remove(value);
		}
		
		this.kvmap.remove(key);
	}
}