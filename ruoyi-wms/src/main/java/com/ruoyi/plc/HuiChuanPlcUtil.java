package com.ruoyi.plc;

//import HslCommunication.Core.Transfer.DataFormat;
//import HslCommunication.Core.Types.OperateResult;
//import HslCommunication.Core.Types.OperateResultExOne;
//import HslCommunication.Profinet.Inovance.InovanceSerialOverTcp;
//import HslCommunication.Profinet.Inovance.InovanceSeries;

public class HuiChuanPlcUtil {


    ///**
    // * 获取连接对象
    // * @param ip
    // * @param port
    // * @param isAddressStartWithZero
    // * @param dataFormat
    // * @param series
    // * @return
    // * @throws Exception
    // */
    //public static InovanceSerialOverTcp getConnection(String ip,Integer port,boolean isAddressStartWithZero,DataFormat dataFormat,InovanceSeries series) throws Exception {
    //    InovanceSerialOverTcp plc = new InovanceSerialOverTcp();
    //    plc.setIpAddress(ip);
    //    plc.setPort(port);
    //    plc.setAddressStartWithZero(isAddressStartWithZero);
    //    plc.setDataFormat(dataFormat);
    //    plc.setSeries(series);
    //
    //    OperateResult connect = plc.ConnectServer();
    //
    //    if (!connect.IsSuccess) {
    //        throw new Exception("Connect Failed\r\nReason:" + connect.ToMessageShowString());
    //    }
    //    return plc;
    //}
    //
    //
    ///**
    // * 读取boolean类型数据
    // * @param plc
    // * @param address
    // * @return
    // * @throws Exception
    // */
    //public static boolean readBoolean(InovanceSerialOverTcp plc,String address) throws Exception {
    //    OperateResultExOne<Boolean> read = plc.ReadBool(address);
    //    if(!read.IsSuccess){
    //        throw new Exception("Read Failed:" + read.ToMessageShowString());
    //    }
    //    return read.Content.booleanValue();
    //}
    //
    //public static void main(String[] args) {
    //    try {
    //        InovanceSerialOverTcp plc = HuiChuanPlcUtil.getConnection("192.168.0.52", 502, true,
    //                DataFormat.CDAB, InovanceSeries.AM);
    //
    //        boolean address1 = HuiChuanPlcUtil.readBoolean(plc, "0.0");
    //
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //
    //}

}
