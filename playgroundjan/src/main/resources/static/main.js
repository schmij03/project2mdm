function formatResult(data) {
    let result = '<strong>Classification Result:</strong><br><br><table class="table table-bordered">';
    result += '<tr><th>Class</th><th>Probability</th></tr>';
    data.forEach(item => {
        result += `<tr><td>${item.className}</td><td>${item.probability.toFixed(4)}</td></tr>`;
    });
    result += '</table>';
    return result;
}

function readURL(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();

        reader.onload = function(e) {
            $('#image-container').html(`<img src="${e.target.result}" alt="Selected Image" class="image-preview">`);
        }

        reader.readAsDataURL(input.files[0]);
    }
}

$(document).ready(function () {
    $('#file').change(function() {
        readURL(this);
        $('#file-name').text(this.files[0].name);
    });

    $('#classification-form').on('submit', function (e) {
        e.preventDefault();
        const formData = new FormData(this);
        $.ajax({
            type: 'POST',
            url: '/classify',
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
                const parsedData = JSON.parse(data);
                $('#result').html(formatResult(parsedData));
            },
            error: function () {
                $('#result').html('<strong>Error:</strong> Unable to classify the image.');
            }
        });
    });

    $('#reset-button').click(function () {
        $('#classification-form')[0].reset();
        $('#file-name').text('');
        $('#image-container').html('');
        $('#result').html('');
    });
});
