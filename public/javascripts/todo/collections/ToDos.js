var app = app || {};

(function(){
    app.collections.ToDos = Backbone.Collection.extend({
        initialize: function(){
            this.add({title: "Learn Javascript basics"})
            this.add({title: "Go to backbonejs.org"})
            this.add({title: "Develop a Backbone application"})
        },
        model: app.models.ToDo,
        up: function(index){
            if(index > 0){
                var tmp = this.models[index - 1]
                this.models[index -1] = this.models[index]
                this.models[index] = tmp;
                this.trigger('change')
            }
        },
        download: function(index){
            if(index < this.models.length - 1){
                var tmp = this.models[index + 1]
                this.models[index + 1] = this.models[index]
                this.models[index] = tmp;
                this.trigger('change')
            }
        },
        archive: function(archived, index){
            this.models[index].set("archived", archived)
        },
        changeStatus: function(done, index){
            this.models[index].set("done", done)
        }
    });
})();
