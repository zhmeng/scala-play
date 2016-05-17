/**
 * Created by wjr on 16-5-17.
 */
require.config({
    // The shim config allows us to configure dependencies for
    // scripts that do not call define() to register a module
    shim: {
        underscore: {
            exports: '_'
        },
        backbone: {
            deps: [
                'underscore',
                'jquery'
            ],
            exports: 'Backbone'
        },
        backboneLocalstorage: {
            deps: ['backbone'],
            exports: 'Store'
        }
    },
    paths: {
        jquery: '//cdn.bootcss.com/jquery/2.2.3/jquery',
        text: '//cdn.bootcss.com/require-text/2.0.12/text'
    }
});
define(
    "friends.data",
    [ /** no dependencies. **/ ],
    [
        {
            "id": 1,
            "name": "Sarah",
            "age": 35
        },
        {
            "id": 2,
            "name": "Tricia",
            "age": 38
        },
        {
            "id": 3,
            "name": "Joanna",
            "age": 29
        },
        {
            "id": 4,
            "name": "Libby",
            "age": 37
        }
    ]
)

require([
    'jquery',
    'friends.data',
    'text!/require/remoteTpl'
], function($, friendsData, friendHtml){
    var firendsList = $('ul.friends')
    var template = $(friendHtml)
    var buffer = $.map(friendsData, function(friendData, index){
        var friend = template.clone();
        friend.find("span.name").text(
            friendData.name
        );
        friend.find("span.age span.value").text(
            friendData.age
        );
        return (friend.get());
    });
    firendsList.append(buffer)
});