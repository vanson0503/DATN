@extends('../layout')

@section('content')
<style>
    .btn-status,
    .btn-role,
    .btn-action {
        cursor: pointer;
        pointer-events: auto;
    }
</style>
<div class="container-xxl flex-grow-1 container-p-y">
    <div class="card">
        <h5 class="card-header">Admin List</h5>
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Image</th>
                        <th>Name</th>
                        <th>Status</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody id="adminList">
                </tbody>
            </table>
        </div>
    </div>

</div>


<script>
    document.addEventListener("DOMContentLoaded", function () {
        fetchAdmins();
    });

    function fetchAdmins() {
        fetch('http://localhost/food-api/public/api/admins')
            .then(response => response.json())
            .then(data => displayAdmins(data))
            .catch(error => console.error('Error:', error));
    }

    function displayAdmins(admins) {
        const adminList = document.getElementById('adminList');
        adminList.innerHTML = ""
        admins.forEach(admin => {
            const row = document.createElement('tr');
            const imageCell = document.createElement('td');
            const image = document.createElement('img');
            image.src = admin.image_url;
            image.style.width = '40px';
            image.style.height = 'auto';
            image.style.borderRadius = '50%';
            imageCell.appendChild(image);

            const nameCell = document.createElement('td');
            nameCell.textContent = admin.username;

            const statusCell = document.createElement('td');
            const statusBtn = document.createElement('button');
            statusBtn.className = `btn btn-status btn-${admin.status === 'active' ? 'success' : 'secondary'}`;
            statusBtn.textContent = capitalizeFirstLetter(admin.status);
            statusCell.appendChild(statusBtn);

            const roleCell = document.createElement('td');
            const roleBtn = document.createElement('button');
            roleBtn.className = `btn btn-role ${getRoleButtonClass(admin.role)}`;
            roleBtn.textContent = capitalizeFirstLetter(admin.role);
            roleCell.appendChild(roleBtn);

            const actionsCell = document.createElement('td');
            const editBtn = document.createElement('button');
            editBtn.setAttribute('title', 'Edit');
            editBtn.setAttribute('data-bs-toggle', 'modal');
            editBtn.setAttribute('data-bs-target', '#editAdminModal');
            editBtn.className = 'btn btn-action btn-warning';
            editBtn.innerHTML = '<i class="bx bx-edit-alt" ></i>'; // Font Awesome edit icon
            editBtn.onclick = function () {
                document.getElementById('username').value = admin.username;
                document.getElementById('id').value = admin.id;
                document.getElementById('status').value = admin.status;
                document.getElementById('role').value = admin.role;
            };
            actionsCell.appendChild(editBtn);

            const resetPasswordBtn = document.createElement('button');
            resetPasswordBtn.setAttribute('title', 'Reset password');
            resetPasswordBtn.setAttribute('data-bs-toggle', 'modal');
            resetPasswordBtn.setAttribute('data-bs-target', '#resetPasswordModal');
            resetPasswordBtn.className = 'btn btn-action btn-info';
            resetPasswordBtn.innerHTML = '<i class="bx bx-refresh" ></i>'; // Font Awesome reset icon
            resetPasswordBtn.onclick = function () {
                document.getElementById('id2').value = admin.id;
            };
            actionsCell.appendChild(resetPasswordBtn);

            row.appendChild(imageCell);
            row.appendChild(nameCell);
            row.appendChild(statusCell);
            row.appendChild(roleCell);
            row.appendChild(actionsCell);

            adminList.appendChild(row);
        });
    }

    function getRoleButtonClass(role) {
        switch (role.toLowerCase()) {
            case 'staff':
                return 'btn-info';
            case 'manager':
                return 'btn-primary';
            case 'admin':
                return 'btn-danger';
            default:
                return 'btn-secondary';
        }
    }

    function capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }


    function saveAdminChanges() {
        const id = document.getElementById('id').value
        // Tạo đối tượng JavaScript với dữ liệu từ form
        const adminData = {
            username: document.getElementById('username').value,
            status: document.getElementById('status').value,
            role: document.getElementById('role').value
        };

        // Gửi yêu cầu fetch đến API để tạo mới một admin
        fetch('http://localhost/food-api/public/api/admin/update/' + id, {  // URL phụ thuộc vào thiết kế của API backend
            method: 'POST',  // Phương thức POST dùng để tạo mới dữ liệu
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',  // Đảm bảo API trả về dữ liệu dạng JSON
            },
            body: JSON.stringify(adminData)  // Chuyển đổi dữ liệu form thành chuỗi JSON
        })
            .then(response => {
                if (!response.ok) {  // Kiểm tra nếu có lỗi từ phía server, ví dụ như lỗi validation
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                fetchAdmins()
                showToast("Admin", "Sửa thành công!", "bg-success")
                $('#editAdminModal').modal('hide');  // Đóng modal sau khi cập nhật
                // Tải lại danh sách admin hoặc cập nhật giao diện người dùng tương ứng
            })
            .catch((error) => {
                showToast("Admin", "Sửa thành công!", "bg-danger")
                alert('Error creating admin.');  // Hiển thị thông báo lỗi
            });
    }

    function submitNewPassword() {
        const id = document.getElementById('id2').value;
        const password = document.getElementById('newPassword').value;
        const confirmNewPassword = document.getElementById('confirmNewPassword').value;

        // Đảm bảo mật khẩu mới được nhập đúng và xác nhận
        if (password == "" || confirmNewPassword == "") {
            alert('Passwords do not empty.');
            return;
        }
        if (password !== confirmNewPassword) {
            alert('Passwords do not match.');
            return;
        }

        // Yêu cầu đến server để đặt lại mật khẩu
        // Thay thế URL_API với đường dẫn API đúng của bạn
        fetch('http://localhost/food-api/public/api/admin/updatepassword/' + id, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
            body: JSON.stringify({ "password": password })
        })
            .then(response => response.json())
            .then(data => {
                showToast("Admin", "Reset mật khẩu thành công!", "bg-success")
                $('#resetPasswordModal').modal('hide');
                document.getElementById('newPassword').value = "";
                document.getElementById('confirmNewPassword').value = "";
            })
            .catch(error => {
                showToast("Admin", "Reset mật khẩu thất bại!", "bg-danger")
                alert('Failed to reset password.');
            });
    }



</script>

<!-- Modal for Editing Admin -->
<div class="modal fade" id="editAdminModal" tabindex="-1" aria-labelledby="editAdminModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="editAdminModalLabel">Edit Admin</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="editAdminForm">
                    <input type="hidden" id="id" name="id" />
                    <div class="mb-3">
                        <label for="username" class="form-label">Name</label>
                        <input type="text" class="form-control" id="username" required>
                    </div>
                    <div class="mb-3">
                        <label for="status" class="form-label">Status</label>
                        <select class="form-select" id="status">
                            <option value="active">Active</option>
                            <option value="blocked">Blocked</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="role" class="form-label">Role</label>
                        <select class="form-select" id="role">
                            <option value="staff">Staff</option>
                            <option value="manager">Manager</option>
                            <option value="admin">Admin</option>
                        </select>
                    </div>
                    <!-- Additional fields can be added here -->
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" onclick="saveAdminChanges()">Save Changes</button>
            </div>
        </div>
    </div>
</div>

<!-- Modal Reset Password -->
<div class="modal fade" id="resetPasswordModal" tabindex="-1" aria-labelledby="resetPasswordModalLabel"
    aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="resetPasswordModalLabel">Reset Password</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="resetPasswordForm">
                    <input type="hidden" id="id2" name="id2" />
                    <div class="form-password-toggle">
                        <label class="form-label" for="newPassword">New Password</label>
                        <div class="input-group">
                            <input type="password" class="form-control" id="newPassword"
                                placeholder="&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;"
                                aria-describedby="basic-default-password2" />
                            <span id="basic-default-password2" class="input-group-text cursor-pointer"><i
                                    class="bx bx-hide"></i></span>
                        </div>
                    </div>
                    <div class="form-password-toggle">
                        <label class="form-label" for="confirmNewPassword">Confirm New Password</label>
                        <div class="input-group">
                            <input type="password" class="form-control" id="confirmNewPassword"
                                placeholder="&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;&#xb7;"
                                aria-describedby="basic-default-password2" />
                            <span id="basic-default-password2" class="input-group-text cursor-pointer"><i
                                    class="bx bx-hide"></i></span>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary" onclick="submitNewPassword()">Submit</button>
            </div>
        </div>
    </div>
</div>


@endsection