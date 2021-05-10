package com.airing.spring.cloud.base.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * HTTP响应包装类
 *
 * @author GEYI
 * @date 2021年04月13日 19:13
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    ByteArrayOutputStream output;
    FilterServletOutputStream filterOutput;

    public ResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        output = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (filterOutput == null) {
            filterOutput = new FilterServletOutputStream(output);
        }
        return filterOutput;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(new OutputStreamWriter(getOutputStream()));
    }

    public byte[] getDataStream() {
        return output.toByteArray();
    }

    class FilterServletOutputStream extends ServletOutputStream {
        DataOutputStream output;

        public FilterServletOutputStream(OutputStream output) {
            this.output = new DataOutputStream(output);
        }

        @Override
        public void write(int arg0) throws IOException {
            output.write(arg0);
        }

        @Override
        public void write(byte[] arg0, int arg1, int arg2) throws IOException {
            output.write(arg0, arg1, arg2);
        }

        @Override
        public void write(byte[] arg0) throws IOException {
            output.write(arg0);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

}
