package edu.asu.utils;

public class Properties {
    //AWS request queue
    private static String msgReqQueue="https://sqs.us-east-1.amazonaws.com/025818375297/requestQueue";
    //aws response queue
    private static String msgResQueue="https://sqs.us-east-1.amazonaws.com/025818375297/responseQueue";
    public String getMsgReqQueue() {
        return this.msgReqQueue;
    }

    public String getMsgResQueue() {
        return this.msgResQueue;
    }
}
