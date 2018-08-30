Ext.namespace("Ext.ux");

Ext.ux.IFrameComponent = Ext.extend(Ext.BoxComponent, {
     onRender : function(ct, position){
     	var iframeid='iframe-'+ this.id;
     	if (this.wiframeid){
     		iframeid=this.wiframeid;
     	}
     	
          this.el = ct.createChild({tag: 'iframe', id:iframeid, name:iframeid, frameBorder: 0,allowtransparency:true, src: this.url});
     },
	destroy:function(){
		this.el.dom.src="about:blank";
		Ext.ux.IFrameComponent.superclass.destroy.apply(this, arguments);
	},
	refreshIframe:function(nurl){
		this.el.dom.src="about:blank";
		this.url=nurl;
		this.el.dom.src=this.url;
	}
});