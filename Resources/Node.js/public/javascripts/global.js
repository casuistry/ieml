
$(document).ready(function() {
    // Populate the user table on initial page load
    populateTable();

});

// Fill table with data
function populateTable() {

    // Empty content string
    var tableContent = '';

    var dummy = 'dummy';
 
    // jQuery AJAX call for JSON
    $.getJSON( '/ieml', function( data ) {

        // For each item in our JSON, add a table row and cells to the content string
        $.each(data, function(){
            tableContent += '<tr>';
            tableContent += '<td><a href="#" class="linkshowuser" rel="' + this.ieml + '">' + this.ieml + '</a></td>';

    for(var i=0;i<this.terms.length;i++){
        var obj = this.terms[i];
        for(var key in obj){
            var attrName = key;
            var attrValue = obj[key];
            if (attrName === 'means') tableContent += '<td>' + attrValue + '</td>';
        }
    }
            //tableContent += '<td>' + '</td>';
            //tableContent += '<td>' + '</td>';

            tableContent += '<td><a href="#" class="linkdeleteuser" rel="' + this._id + '">delete</a></td>';
            tableContent += '</tr>';
        });

        // Inject the whole content string into our existing HTML table
        $('#userList table tbody').html(tableContent);
    });
};

function logArrayElements(element, index, array) {
  console.log('a[' + index + ']');
};