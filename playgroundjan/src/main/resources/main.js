$(document).ready(function () {
    var progressBar = $(".progress-bar");
    var progressContainer = $(".progress");
    var imagesContainer = $("#images");
    var resetButton = $("#resetButton");

    $.get("/classes", function (data) {
        for (var key in data) {
            if (data.hasOwnProperty(key)) {
                $('#classSelect').append($('<option>', {
                    value: key,
                    text: data[key]
                }));
            }
        }
    });

    $("#generateButton").click(function () {
        var classId = $("#classSelect").val();
        var numImages = $("#numImages").val();

        // Disable the generate button and show the progress bar
        $("#generateButton").prop("disabled", true);
        progressContainer.show();

        // Reset progress bar to 0
        progressBar.css("width", "0%");
        progressBar.attr("aria-valuenow", 0);

        // Show loading animation
        imagesContainer.html('<div class="loading-animation"></div>');

        // Send the generate request
        $.ajax({
            url: "/generate",
            method: "POST",
            data: {
                classId: classId,
                numImages: numImages
            },
            success: function (data) {
                // Generate successful, show the images
                imagesContainer.empty();
                for (var i = 0; i < data.length; i++) {
                    imagesContainer.append('<img src="' + data[i] + '" class="img-thumbnail mx-2" alt="Generated Image">');
                }
            },
            error: function () {
                // Error occurred, display error message
                imagesContainer.html('<div class="alert alert-danger" role="alert">Error generating images. Please try again.</div>');
            },
            complete: function () {
                // Enable the generate button and hide the progress bar
                $("#generateButton").prop("disabled", false);
                progressContainer.hide();
            },
            xhr: function () {
                // Track upload progress
                var xhr = new XMLHttpRequest();
                xhr.upload.onprogress = function (event) {
                    if (event.lengthComputable) {
                        var percent = Math.round((event.loaded / event.total) * 100);
                        progressBar.css("width", percent + "%");
                        progressBar.attr("aria-valuenow", percent);
                    }
                };
                return xhr;
            }
        });
    });

    resetButton.click(function () {
        // Reset the form and clear the images
        imagesContainer.empty();
        $("#classSelect").val("");
        $("#numImages").val("1");
    });
});
