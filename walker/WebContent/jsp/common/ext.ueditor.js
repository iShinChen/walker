Ueditor = Ext.extend(Ext.form.Field ,{
    id : 'editor',
    defaultAutoCreate : {tag: 'div'},
    constructor : function(cfg){
        Ext.apply(this,cfg);
        this.listeners = {
            render : function(cmp) {
                var ueditor = new UE.ui.Editor({zIndex:10000,initialFrameWidth:"100%",initialFrameHeight : 280,scaleEnabled:true});
                this.ueditor = ueditor;
                ueditor.render(cmp.id);
                if(this.originalValue){
                    var v = this.originalValue;
                    ueditor.ready(function(){
                        ueditor.setContent(v);
                    });
                }
            },
            scope : this
        };

        Ueditor.superclass.constructor.call(this);
    },
    /**
     * 重写setValue方法 主要为修改设置值
     * */
    setValue : function(value){
        if(!this.ueditor){
            this.originalValue = value;
        }else{
            this.ueditor.setContent(value);
        }
    }
});