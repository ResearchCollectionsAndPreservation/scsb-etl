/**
 * Created by SheikS on 6/20/2016.
 */
var intervalID;

jQuery(document).ready(function ($) {
    $("#bulkingest-form").submit(function (event) {
        event.preventDefault();
        bulkIngest();
    });
    $('#dateFrom, #dateTo').datepicker({
        format: "yyyy/mm/dd"
    });
    populateInstitutionForImport();
});


function refresh() {
    var autoRefresh = $('#autoRefresh').is(':checked');
    if(autoRefresh) {
        intervalID= setInterval(function () {
            updateStatus();
        }, 60000);
    } else {
        clearInterval(intervalID);
    }
}

function bulkIngest() {
    var $form = $('#bulkingest-form');
    $("#submit").attr('disabled', 'disabled');
    $.ajax({
        url: $form.attr('action'),
        type: 'post',
        data: $form.serialize(),
        success: function (response) {
            $("#submit").removeAttr('disabled');
        }
    });
    setTimeout(function(){
    }, 2000);
    updateStatus();
}


function updateStatus() {
    var request = $.ajax({
        url: "etlDataLoader/status",
        type: "GET",
        contentType: "application/json"
    });
    request.done(function (msg) {
        document.getElementById("bulkIngestStatus").value = msg;
    });
}

function populateInstitutionForImport() {
       getInstitutions('institutionName');
}
function populateInstitutionForReports() {
       getInstitutions('reportInstitutionName');
}
function getInstitutions(selectId) {
       $('#'+selectId).empty();
       var request = $.ajax({
            url: "etlDataLoad/institutions",
            type: "GET",
            contentType: "application/json"
        });
        request.done(function (response) {
             $.each(response , function(index, val) {
               $('#'+selectId).append('<option value="' + val + '">' + val+ '</option>');
             });
        });
}


