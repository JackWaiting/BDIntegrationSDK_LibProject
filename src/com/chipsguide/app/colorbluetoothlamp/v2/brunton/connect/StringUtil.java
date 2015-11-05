package com.chipsguide.app.colorbluetoothlamp.v2.brunton.connect;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothDevice;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.application.CustomApplication;

public class StringUtil
{
	public static boolean isMacMatched(String mac)
	{
		if (mac != null)
		{
			if (mac.startsWith(CustomApplication.MAC_ADDRESS_FILTER_PREFIX))
			{
				return true;
			}
		}
		return false;
	}
	
	//适应多个布局的list
	 public static List<ConnectMessage> getListConnectMessage(List<ConnectInfo> daolist)
	 {
		 List<ConnectMessage> msglist = new ArrayList<ConnectMessage> ();
		for(int i=0;i<daolist.size();i++)
		{
			ConnectMessage msg = new ConnectMessage();
			msg.setType(ConnectMessage.TYPE_SEPARATOR);
			msg.setBluetoothName(daolist.get(i).getContent_name());
			msglist.add(msg);
		}
		 return msglist;
	 }
	 
	 
		//把当前的数据库的数据和新搜索的数据转化一个适应多个布局的list
	 public static boolean getListConnectMessage(List<ConnectInfo> daolist,BluetoothDevice searchBluetooth)
	 {
		 Set<String> set = new HashSet<String>();
		 List<ConnectMessage> msglist = new ArrayList<ConnectMessage> ();
		for(int i=0;i<daolist.size();i++)
		{
			ConnectMessage msg = new ConnectMessage();
			msg.setType(ConnectMessage.TYPE_SEPARATOR);
			msg.setBluetoothName(daolist.get(i).getContent_name());
			msg.setAddress(daolist.get(i).getMac_address());
			msglist.add(msg);
			
			set.add(daolist.get(i).getMac_address());
		}
		
		if(set.add(searchBluetooth.getAddress()))
		{
			return true;
		}
		return false;
	 }
	
	//把当前的数据库的数据和新搜索的数据转化一个适应多个布局的list
	 public static List<ConnectMessage> getListConnectMessage(List<ConnectInfo> daolist,List<BluetoothDevice> search)
	 {
		 Set<String> set = new HashSet<String>();
		 List<ConnectMessage> msglist = new ArrayList<ConnectMessage> ();
		for(int i=0;i<daolist.size();i++)
		{
			ConnectMessage msg = new ConnectMessage();
			msg.setType(ConnectMessage.TYPE_SEPARATOR);
			msg.setBluetoothName(daolist.get(i).getContent_name());
			msg.setAddress(daolist.get(i).getMac_address());
			msglist.add(msg);
			
			set.add(daolist.get(i).getMac_address());
		}
		
		for(int i=0;i<search.size();i++)
		{
			ConnectMessage msg = new ConnectMessage();
			msg.setType(ConnectMessage.TYPE_ITEM);
			msg.setBluetoothName(search.get(i).getName());
			msg.setAddress(search.get(i).getAddress());
			
			if(set.add(search.get(i).getAddress()))
			{
				msglist.add(msg);
			}
		}
		 return msglist;
	 }
	 
	 /**
	 * 去除重复的蓝牙
	 */
	public static List<BluetoothDevice> removeDuplicateWithOrder(List<BluetoothDevice> listBluetooth)
	{
		if(listBluetooth.size() == 1 && isMacMatched(listBluetooth.get(0).getAddress()))
		{
			return listBluetooth;
		}
		Set<BluetoothDevice> set = new HashSet<BluetoothDevice>();
		List<BluetoothDevice> newList = new ArrayList<BluetoothDevice>();
		for (Iterator<BluetoothDevice> iter = listBluetooth.iterator(); iter.hasNext();)
		{
			BluetoothDevice element = iter.next();
			if (set.add(element) && isMacMatched(element.getAddress()))
			{
					newList.add(element);
			}
		}
		listBluetooth.clear();
		listBluetooth.addAll(newList);
		return listBluetooth;
	}
}
