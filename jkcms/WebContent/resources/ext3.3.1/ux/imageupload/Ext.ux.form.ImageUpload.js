Ext.ns('Ext.ux.form');
/**
 * @author qup
 * @version 1.0
 * @description 图片组件
 * @since 2016-4-5
 * @class Ext.ux.form.ImageUploadField
 * @extends Ext.Container
 */
Ext.ux.form.ImageUploadField = Ext.extend(Ext.Container, {
	id : "",
	height : 300,
	width : 300,
	submitText : '上传',
	cancelText : '取消',
	clearText : '清空',
	//文件是否覆盖
	override : true,
	value : {},
	uploadAction : "",
	uploadParams : {},
	defaultImage : "",
	errorImage : "",
	inputName : "uploadFile",
	submitVisiable : true,
	resloutionVisiable : true,
	fileselected : false,
	initComponent : function() {
		Ext.ux.form.ImageUploadField.superclass.initComponent.call(this, arguments);
		this.addEvents("selected", "uploaded", "unuploaded", "canceled", "beforeupload", "imagenotfound", "uploadclick", "cleared");
		if(!this.hasListener("uploadclick")) {
			this.addListener("uploadclick", this.submit);
		}
	},
	onRender : function(ct, position){
		Ext.ux.form.ImageUploadField.superclass.onRender.call(this, ct, position);
		this.el.addClass('x-ux-form-imageupload');

		this.submitForm = this.findParentByType("form");
		if (this.submitForm == null) {
			Ext.Msg.alert("提示", "ImageUpload控件必须包含在FormPanel控件中!");
			return false;
		}
		
		this.add(this.createImage(true));
		
		var me = this;
		
		var tools = [
			this.createUploadField(true)
		];
		
		if (this.resloutionVisiable) {
			this.resloutionField = new Ext.form.DisplayField({
				id : this.id + '-resloution',
				cls : 'x-ux-form-imageupload-resloution'
			});
			tools.push(this.resloutionField);
		}
		tools.push("->");
		
		this.clearBtn = new Ext.Button({ 
            text: me.clearText,
            disabled : true,
            handler: function(t, e) {
				me.setValue(null);
            	me.reset();
				
				me.clearBtn.setDisabled(true);
        		me.fireEvent('cleared', me);
        	}
        });
		
		tools.push(this.clearBtn);
		
		if (this.submitVisiable) {
			this.submitBtn = new Ext.Button({
	            text: me.submitText,
	            disabled : true,
	            handler: function(t) {
					me.fireEvent("uploadclick", me, t);
				}
	        });
			tools.push(this.submitBtn);
		}
		
		this.cancelBtn = new Ext.Button({ 
            text: me.cancelText,
            disabled : true,
            handler: function(t, e) {
				me.reset();
        		me.fireEvent('canceled', me);
        	}
        });
		
		tools.push(this.cancelBtn);
		
		this.add(new Ext.Toolbar({
			width : me.width - 2,
			height : 30,
	        items: tools
	    }));
	},
	createImage : function(autoCreate) {
		if (autoCreate) {
			return this.imageField || (function() {
				var initConfig = {
					height : this.height - 32,
					width : this.width - 2
				};
				var me = this;
				this.imageField = new Ext.ux.form.Image(initConfig);
				this.imageField.on("error", function(img) {
					me.fireEvent("imagenotfound", me, img.imageSrc);
				});
				return this.imageField;
			}.createDelegate(this))();
		} else {
			return this.imageField || {};
		}
	},
	createUploadField : function(autoCreate) {
		if (autoCreate) {
			return this.uploadField || (function() {
				// Ext.ux.Form.FileUploadField是官方的插件,可以再例子里看到它
				this.uploadField = new Ext.ux.form.FileUploadField({
					name : this.inputName,
					buttonText : "浏览",
					buttonOnly : true,
					width : 40
				});
				var me = this;
				this.uploadField.on("fileselected", function(obj, value) {
					if (!me.isPicture(value)) {
						Ext.Msg.alert("提示", "无效的图片文件,请重新选择!");
						me.reset();
					}
					else {
						me.fileselected = true;
						if (me.submitBtn) {
							me.submitBtn.setDisabled(false);
						}
						me.cancelBtn.setDisabled(false);
						me.fireEvent("selected", obj, value);
					}
				}, this);
				return this.uploadField;
			}.createDelegate(this))();
		} else {
			return this.uploadField || {};
		}
	},
	submit : function() {
		if (!this.fileselected) {
			Ext.Msg.alert("提示", "请先选择需要上传的图片文件!");
			return false;
		}
		if (!this.fireEvent("beforeupload", this)) {
			return false;
		}
		var params = {};
		if (this.uploadParams) {
			params = this.uploadParams;
		}
		if (this.override) {
			params.fileUrl = this.fileUrl;
		}
		
		// 解决表单中多imageuploadfield上传文件冲突问题,since 2016-5-13 begin
		var imageUploads = this.submitForm.findByType("imageuploadfield");
		if (imageUploads.length > 1) {
			for ( var i = 0; i < imageUploads.length; i++) {
				if(imageUploads[i] == this) {
					imageUploads[i].uploadField.resetInputName();
				}
				else {
					imageUploads[i].uploadField.removeInputName();
				}
			}
		}
		// 解决表单中多imageuploadfield上传文件冲突问题,since 2016-5-13 end
		
		var me = this;
		this.submitForm.getForm().submit({
			method: "POST",
			waitMsg: "图片上传中...",
			url : me.uploadAction,
			params : params,
			timeout : 30,
			success: function(form, action) {
				if (action.result.success) {
					me.setValue(action.result.data);
					me.fireEvent('uploaded', me, action.result.data);
				}
				else {
					me.fireEvent('unuploaded', me, action.result);
				}
			},
			failure: function(form, action) {
				me.fireEvent('unuploaded', me, action);
			}
		});
	},
	isPicture : function(str) {
		//判断是否是图片 - strFilter必须是小写列举
		var strFilter = ['.jpeg', '.gif', '.jpg','.png','.bmp'];
		var p = str.lastIndexOf(".");
		var strPostfix = str.substring(p);        
		for(var i=0; i < strFilter.length; i++) {
		    if(strFilter[i] == strPostfix.toLowerCase())
		    {
		        return true;
		    }
		}
		return false;            
    },
	reset: function() {
		this.fileselected = false;
		this.value = {};
    	if (this.submitBtn) {
    		this.submitBtn.setDisabled(true);
		}
		this.clearBtn.setDisabled(true);
		this.cancelBtn.setDisabled(true);
		this.uploadField.reset();
	},
	getValue : function (){
		return this.value == null ? {} : this.value;
	},
	setValue : function (obj){
		this.reset();

		if (this.imageField) {
			this.imageField.setDefaultImage();
		}
		if(this.resloutionField) {
			this.resloutionField.setValue('');
		}
		
		if(obj) {
			this.value = obj;
			this.viewUrl = obj.viewUrl;
			this.resloution = obj.resloution;
			
			if (obj.viewUrl) {
				if (this.imageField) {
					this.imageField.setImage(obj.viewUrl);
				}

				this.clearBtn.setDisabled(false);
			}
			if (obj.resloution) {
				if (this.resloutionField) {
					this.resloutionField.setValue(obj.resloution);
				}
			}
		}
	},
	getRootPath : function () 
	{ 
		var pathName = window.location.pathname.substring(1);
		var webName = pathName == '' ? '' : pathName.substring(0, pathName.indexOf('/')); 
		return '/'+ webName; 
	}
});

Ext.reg('imageuploadfield', Ext.ux.form.ImageUploadField);