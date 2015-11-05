package com.chipsguide.app.colorbluetoothlamp.v2.brunton.connect;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ConnectInfo implements Serializable {
	private String id;// 唯一标识
	private String name;// 蓝牙名称
	private String content_name;// 蓝牙备注名称
	private String mac_address;// Mac地址
	private String isconnect;// 是否连接：1连接、0不连接
	private long date;// 最后连接日期
	private String device;// 设备

	public ConnectInfo()
	{
		this.name = null;
		this.content_name = null;
		this.mac_address = null;
		this.isconnect = "0";
		this.date = System.currentTimeMillis();
	}
	
	public ConnectInfo(String id,String name,String conetentName,String macAddress,String isconnect,long date,
			String devce)
	{
		this.id = id;
		this.name = name;
		this.content_name = conetentName;
		this.mac_address = macAddress;
		this.isconnect = isconnect;
		this.date = date;
		this.device = devce;
	}

	public String getContent_name()
	{
		return content_name;
	}

	public void setContent_name(String content_name)
	{
		this.content_name = content_name;
	}

	public String getMac_address()
	{
		return mac_address;
	}

	public void setMac_address(String mac_address)
	{
		this.mac_address = mac_address;
	}

	public String getIsconnect()
	{
		return isconnect;
	}

	public void setIsconnect(String isconnect)
	{
		this.isconnect = isconnect;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDevice()
	{
		return device;
	}

	public void setDevice(String device)
	{
		this.device = device;
	}

	public long getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = date;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	@Override
	public String toString()
	{
		return "ConnectInfo [id=" + id + ", name=" + name + ", content_name="
				+ content_name + ", mac_address=" + mac_address
				+ ", isconnect=" + isconnect + ", device=" + device + ", date="
				+ date + "]";
	}
}
