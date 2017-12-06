Ext.ns('Ext.ux.form');
/**
 * @author qup
 * @version 1.0
 * @description 图片组件
 * @since 2016-4-5
 * @class Ext.ux.form.Image
 * @extends Ext.BoxComponent
 */
Ext.ux.form.Image = Ext.extend(Ext.BoxComponent, {
	imageSrc : undefined,
	defaultImage : "resources/images/image_default.jpg",
	errorImage : "resources/images/image-404.png",
	initComponent : function() {
		Ext.ux.form.Image.superclass.initComponent.call(this, arguments);
		this.addEvents("error", "load");
	},
    onRender : function(ct, position){
		Ext.ux.form.Image.superclass.onRender.call(this, ct, position);
		this.getXTemplate().overwrite(this.getEl(), this.getImageData());
		this.el.addClass('x-ux-form-image');
		this.image = this.el.child("img").dom;
	},
	getXTemplate : function() {
		return this.xtpl
				|| (function() {
					this.xtpl = new Ext.XTemplate(
							"<div class='x-ux-form-imagectl'><img id='{id}' height='{height}' width='{width}' src='{imageSrc}' border='{border}' /></div>");
					return this.xtpl;
				}.createDelegate(this))();
	},
	getImageData : function() {
		return this.imageData || (function() {
			var src = "";
			if (this.imageSrc) {
				src = this.imageSrc;
			}
			else {
				src = this.defaultImage;
			}
			
			this.imageData = {
				id : this.getId() + "_img",
				height : this.height,
				width : this.width,
				border : 0,
				imageSrc : src
			};
			return this.imageData;
		}.createDelegate(this))();
	},
	setDefaultImage : function() {
		this.getEl().unmask();
		this.imageSrc = "";
		this.image.removeAttribute("title");
		this.image.style.cursor = "";
		this.image.onclick = Ext.emptyFn;
		this.image.onload = Ext.emptyFn;
		this.image.src = this.defaultImage;
	},
	setErrorImage : function() {
		this.getEl().unmask();
		this.image.onload = Ext.emptyFn;
		this.image.onclick = Ext.emptyFn;
		this.image.src = this.errorImage;
		
		this.fireEvent('error', this);
	},
	setImage : function(src) {
		this.getEl().unmask();
		if (src && src.trim() != "") {
			this.imageSrc = src.trim();
			
			var me = this;
			Ext.Ajax.request( {
				url : 'pictureExists.do',
				method : 'post',
				success : function(response) {
					var result = Ext.decode(response.responseText);
					if (result.success) {
						if(result.exists) {
							me.image.onload = function(){
								me.onLoad();
							};
							
							me.getEl().mask("图片加载中", "x-mask-loading");
							me.image.src = me.imageSrc;
						}
						else {
							me.setErrorImage();
						}
					}
					else {
						Ext.Msg.alert("提示", result.errorMsg);
					}
				},
				params : {
					url : src
				}
			});
		}
		else {
			this.setDefaultImage();
		}
	},
	onLoad : function() {
		this.getEl().unmask();
		
		this.image.onload = Ext.emptyFn;
		
		if (this.imageSrc) { 
			var me = this;
			this.image.setAttribute("title", "点击原尺寸查看");
			this.image.style.cursor = "pointer";
			this.image.onclick = function() {
				var initConfig = {
					constrain : true,
					layout: 'border',
					maximized : true,
					minimizable : false,
					maximizable : false,
					items: [
						new top.Ext.Panel({
							closable : true,
							autoScroll : false,
							layout:'fit',
							region: 'center',
							items: [
								new top.Ext.ux.IFrameComponent({
									url: me.imageSrc
								})
							]
						})
					]
				};
				new top.Ext.Window(initConfig).show();
			};
			
			this.fireEvent('load', this);
		} else {
			this.image.removeAttribute("title");
			this.image.style.cursor = "";
			this.image.onclick = Ext.emptyFn;
		}
	}
});

Ext.reg("ximage", Ext.ux.form.Image);