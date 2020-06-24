package cn.qd.peiwen.network.soap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by nick on 2018/6/17.
 */

public class SoapRequest {
    private String url;
    private int timeout;
    private boolean debug;
    private boolean dotNet;
    private String namespace;
    private String methodName;
    private Map<String, Object> parameters = new HashMap<>();

    public SoapRequest() {

    }

    public SoapRequest url(String url) {
        this.url = url;
        return this;
    }

    public SoapRequest timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public SoapRequest debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public SoapRequest dotNet(boolean dotNet) {
        this.dotNet = dotNet;
        return this;
    }

    public SoapRequest namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public SoapRequest methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public SoapRequest appendParameter(String key,Object value) {
        this.parameters.put(key,value);
        return this;
    }

    public SoapObject request() throws IOException, XmlPullParserException {
        HttpTransportSE transport = new HttpTransportSE(this.url, this.timeout);
        transport.debug = this.debug;
        SoapEnvelope envelope = this.buildEnvelope();
        transport.call(this.soapAction(), envelope);
        return (SoapObject) envelope.bodyIn;
    }

    private String soapAction() {
        return this.namespace + this.methodName;
    }

    private SoapSerializationEnvelope buildEnvelope() {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = this.dotNet;
        envelope.setOutputSoapObject(this.buildRequest());
        return envelope;
    }

    private SoapObject buildRequest() {
        SoapObject request = new SoapObject(this.namespace,this.methodName);
        if (null != this.parameters && !this.parameters.isEmpty()) {
            for (String key : this.parameters.keySet()) {
                request.addProperty(key, this.parameters.get(key));
            }
        }
        return request;
    }

}
