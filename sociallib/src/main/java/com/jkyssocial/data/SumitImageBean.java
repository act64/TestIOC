package com.jkyssocial.data;

/**
 * Created by xiaoke on 16/11/25.
 */

public class SumitImageBean {
    /**
     * header : {"code":2000,"message":"OK"}
     * body : {"file_path":"/uploadfile/1525418_1480061716218_file"}
     */

    private HeaderBean header;
    private BodyBean body;

    public HeaderBean getHeader() {
        return header;
    }

    public void setHeader(HeaderBean header) {
        this.header = header;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class HeaderBean {
        /**
         * code : 2000
         * message : OK
         */

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class BodyBean {
        /**
         * file_path : /uploadfile/1525418_1480061716218_file
         */

        private String file_path;

        public String getFile_path() {
            return file_path;
        }

        public void setFile_path(String file_path) {
            this.file_path = file_path;
        }
    }
//        /**
//         * file_path : /uploadfile/1525418_1480061716218_file
//         */
//
//        private String file_path;
//
//        public String getFile_path() {
//            return file_path;
//        }
//
//        public void setFile_path(String file_path) {
//            this.file_path = file_path;
//        }
}
