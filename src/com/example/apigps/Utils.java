/**
 * 
 */
package com.example.apigps;

import com.amap.api.location.AMapLocation;

/**
 * ����������
 * @����ʱ�䣺 2015��11��24�� ����11:46:50
 * @��Ŀ���ƣ� AMapLocationDemo2.x
 * @author hongming.wang
 * @�ļ�����: Utils.java
 * @��������: Utils
 */
public class Utils {
	/**
	 *  ��ʼ��λ
	 */
	public final static int MSG_LOCATION_START = 0;
	/**
	 * ��λ���
	 */
	public final static int MSG_LOCATION_FINISH = 1;
	/**
	 * ֹͣ��λ
	 */
	public final static int MSG_LOCATION_STOP= 2;
	/**
	 * ��ʾTEXT
	 */
	public final static int MSG_TEXT_SHOW= 3;
	/**
	 * ��ʾһ��BUTTON
	 */
	public final static int MSG_BUTTON_SHOW_ONE= 4;
	/**
	 * ��ʾ����BUTTON
	 */
	public final static int MSG_BUTTON_SHOW_TWO= 6;
	/**
	 * ��ʾTOAST
	 */
	public final static int MSG_TOAST_SHOW= 5;
	/**
	 * TCP�ɹ�
	 */
	public final static int TCP_FINISH= 1;
	/**
	 * TCP��ʼ
	 */
	public final static int TCP_ERROR= -1;
	/**
	 * ȷ��ǩ����
	 */
	public final static int FLAG_NEWTEAM= 1;
	/**
	 * ǩ��
	 */
	public final static int FLAG_SIGNIN= 0;
	/**
	 * ���ݶ�λ������ض�λ��Ϣ���ַ���
	 * @param loc
	 * @return
	 */public synchronized static String getLocationStr(AMapLocation location){
			if(null == location){
				return null;
			}
			StringBuffer sb = new StringBuffer();
			//errCode����0����λ�ɹ���������Ϊ��λʧ�ܣ�����Ŀ��Բ��չ�����λ������˵��
			if(location.getErrorCode() == 0){
				sb.append("��λ�ɹ�" + "\n");
				sb.append("��λ����: " + location.getLocationType() + "\n");
				sb.append("��    ��    : " + location.getLongitude() + "\n");
				sb.append("γ    ��    : " + location.getLatitude() + "\n");
				sb.append("��    ��    : " + location.getAccuracy() + "��" + "\n");
				sb.append("�ṩ��    : " + location.getProvider() + "\n");
				
				if (location.getProvider().equalsIgnoreCase(
						android.location.LocationManager.GPS_PROVIDER)) {
					// ������Ϣֻ���ṩ����GPSʱ�Ż���
					sb.append("��    ��    : " + location.getSpeed() + "��/��" + "\n");
					sb.append("��    ��    : " + location.getBearing() + "\n");
					// ��ȡ��ǰ�ṩ��λ��������Ǹ���
					sb.append("��    ��    : "
							+ location.getExtras().getInt("satellites", 0) + "\n");
				} else {
					// ������GPSʱ��û��������Ϣ��
					sb.append("��    ��    : " + location.getCountry() + "\n");
					sb.append("ʡ            : " + location.getProvince() + "\n");
					sb.append("��            : " + location.getCity() + "\n");
					sb.append("��            : " + location.getDistrict() + "\n");
					sb.append("��    ַ    : " + location.getAddress() + "\n");
				}
			} else {
				//��λʧ��
				sb.append("��λΪʧ��" + "\n");
				sb.append("������:" + location.getErrorCode() + "\n");
				sb.append("������Ϣ:" + location.getErrorInfo() + "\n");
				sb.append("��������:" + location.getLocationDetail() + "\n");
			}
			return sb.toString();
		}
	}

