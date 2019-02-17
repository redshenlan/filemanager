package com.indigo.filemanager.common;
/**
 * @Description:
 * @author: qiaoyuxi
 * @time: 2019年2月11日 下午2:26:01
 */
public class Constants {

	//错误类型
    public static enum Error {
		DEFAULT {
			public String eval() {
				return "999";
			}
			public String getChinese() {
				return "系统错误";
			}
		};
		public abstract String eval();
		public abstract String getChinese();
	}
    
    //根目录ID
    public static long MENU_ROOT_ID = 999;
    
    //错误类型
    public static enum OperaterType {
		CREATE {
			public String eval() {
				return "01";
			}
			public String getChinese() {
				return "创建";
			}
		},
		READ {
			public String eval() {
				return "02";
			}
			public String getChinese() {
				return "读取";
			}
		},
		UPDATE {
			public String eval() {
				return "03";
			}
			public String getChinese() {
				return "修改";
			}
		},
		DELETE {
			public String eval() {
				return "04";
			}
			public String getChinese() {
				return "删除";
			}
		},
		DOWNLOAD {
			public String eval() {
				return "05";
			}
			public String getChinese() {
				return "下载";
			}
		};
		public abstract String eval();
		public abstract String getChinese();
	}
    
}
