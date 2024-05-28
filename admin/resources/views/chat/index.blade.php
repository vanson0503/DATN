@extends('../layout')

@section('content')

<div class="container mt-5">
        <div id="customers" class="row"></div>
    </div>

    <!-- Optional JavaScript -->
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script>
        
    $(document).ready(function() {
    $.ajax({
        url: BASE_API_URL + 'customers/last-message', // Thay thế với URL thực của API của bạn
        type: 'GET',
        success: function(customers) {
            const customerContainer = $('#customers');
            Object.values(customers).forEach(customer => {
                if(customer.last_message != null) {
                    var id = customer.id;
                    var link = "{{ route('chatdetail', ['id' => '__id__']) }}".replace('__id__', id);
                    customerContainer.append(`
                        <div class="col-12">
                            <div class="card mb-3" style="width: 100%;">
                                <div class="row g-0">
                                <div class="col-md-2 d-flex align-items-center justify-content-center">
                                    <img src="${customer.image_url || 'path/to/default-image.png'}" class="img-fluid rounded-circle" style="width: 100px; height: 100px; object-fit: cover;" alt="${customer.full_name}">
                                </div>
                                    <div class="col-md-10">
                                        <div class="card-body">
                                            <h5 class="card-title">${customer.full_name}</h5>
                                            <p class="card-text">${customer.last_message.content || 'No message available'}</p>
                                            <p class="card-text"><small class="text-muted">Last updated ${customer.last_message.created_at || 'N/A'}</small></p>
                                            <a href="${link}" class="btn btn-primary">View Messages</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `);
                }
            });
        },
        error: function() {
            alert('Error loading customers');
        }
    });
});

</script>



@endsection
