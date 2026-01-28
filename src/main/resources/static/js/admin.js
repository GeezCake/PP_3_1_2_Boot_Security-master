(function () {
    'use strict';

    const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
    const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');

    const CSRF_TOKEN = csrfTokenMeta ? csrfTokenMeta.content : null;
    const CSRF_HEADER = csrfHeaderMeta ? csrfHeaderMeta.content : null;

    const errorAlert = document.getElementById('errorAlert');
    const usersTableBody = document.getElementById('usersTableBody');

    const newUserForm = document.getElementById('newUserForm');
    const newRolesSelect = document.getElementById('newRoles');

    const editUserForm = document.getElementById('editUserForm');
    const editRolesSelect = document.getElementById('editRoles');

    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');

    const API = {
        users: '/api/admin/users',
        roles: '/api/admin/roles'
    };

    let allRoles = [];
    let deleteUserId = null;

    function showError(message) {
        if (!errorAlert) {
            alert(message);
            return;
        }
        errorAlert.textContent = message;
        errorAlert.classList.remove('d-none');
    }

    function hideError() {
        if (!errorAlert) {
            return;
        }
        errorAlert.textContent = '';
        errorAlert.classList.add('d-none');
    }

    function buildHeaders(isJson) {
        const headers = {};
        if (isJson) {
            headers['Content-Type'] = 'application/json';
        }
        if (CSRF_TOKEN && CSRF_HEADER) {
            headers[CSRF_HEADER] = CSRF_TOKEN;
        }
        return headers;
    }

    async function fetchJson(url, options) {
        const res = await fetch(url, options);
        if (!res.ok) {
            const text = await res.text().catch(() => '');
            throw new Error(text || (res.status + ' ' + res.statusText));
        }
        // 204 No Content
        if (res.status === 204) {
            return null;
        }
        return res.json();
    }

    function roleNames(user) {
        return (user.roles || []).map(r => r.name).join(' ');
    }

    function renderUsers(users) {
        usersTableBody.innerHTML = '';

        users.forEach(u => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${u.id ?? ''}</td>
                <td>${u.firstName ?? ''}</td>
                <td>${u.lastName ?? ''}</td>
                <td>${u.age ?? ''}</td>
                <td>${u.email ?? ''}</td>
                <td>${roleNames(u)}</td>
                <td>
                    <button type="button" class="btn btn-info btn-sm btn-edit" data-id="${u.id}">Edit</button>
                </td>
                <td>
                    <button type="button" class="btn btn-danger btn-sm btn-delete" data-id="${u.id}">Delete</button>
                </td>
            `;
            usersTableBody.appendChild(tr);
        });
    }

    function fillRolesSelect(select, selectedIds) {
        const selected = new Set((selectedIds || []).map(Number));
        select.innerHTML = '';
        allRoles.forEach(r => {
            const opt = document.createElement('option');
            opt.value = r.id;
            opt.textContent = r.name;
            if (selected.has(Number(r.id))) {
                opt.selected = true;
            }
            select.appendChild(opt);
        });
    }

    function getSelectedRoleIds(select) {
        return Array.from(select.selectedOptions).map(o => Number(o.value));
    }

    async function loadRoles() {
        allRoles = await fetchJson(API.roles, {
            method: 'GET',
            headers: buildHeaders(false)
        });

        fillRolesSelect(newRolesSelect, []);
        fillRolesSelect(editRolesSelect, []);
    }

    async function loadUsers() {
        const users = await fetchJson(API.users, {
            method: 'GET',
            headers: buildHeaders(false)
        });
        renderUsers(users);
    }

    function switchToUsersTab() {
        const usersTabLink = document.getElementById('users-tab');
        if (usersTabLink) {
            // Bootstrap 4: use jQuery to activate tab
            window.jQuery && window.jQuery(usersTabLink).tab('show');
        }
    }

    async function onCreateUser(e) {
        e.preventDefault();
        hideError();

        const payload = {
            firstName: document.getElementById('newFirstName').value.trim(),
            lastName: document.getElementById('newLastName').value.trim(),
            age: Number(document.getElementById('newAge').value),
            email: document.getElementById('newEmail').value.trim(),
            username: document.getElementById('newUsername').value.trim(),
            password: document.getElementById('newPassword').value,
            roleIds: getSelectedRoleIds(newRolesSelect)
        };

        await fetchJson(API.users, {
            method: 'POST',
            headers: buildHeaders(true),
            body: JSON.stringify(payload)
        });

        newUserForm.reset();
        fillRolesSelect(newRolesSelect, []);
        await loadUsers();
        switchToUsersTab();
    }

    async function openEditModal(userId) {
        hideError();
        const user = await fetchJson(`${API.users}/${userId}`, {
            method: 'GET',
            headers: buildHeaders(false)
        });

        document.getElementById('editId').value = user.id;
        document.getElementById('editFirstName').value = user.firstName ?? '';
        document.getElementById('editLastName').value = user.lastName ?? '';
        document.getElementById('editAge').value = user.age ?? '';
        document.getElementById('editEmail').value = user.email ?? '';
        document.getElementById('editUsername').value = user.username ?? '';
        document.getElementById('editPassword').value = '';

        const selectedRoleIds = (user.roles || []).map(r => r.id);
        fillRolesSelect(editRolesSelect, selectedRoleIds);

        window.jQuery && window.jQuery('#editUserModal').modal('show');
    }

    async function onEditUser(e) {
        e.preventDefault();
        hideError();

        const id = Number(document.getElementById('editId').value);
        const payload = {
            id,
            firstName: document.getElementById('editFirstName').value.trim(),
            lastName: document.getElementById('editLastName').value.trim(),
            age: Number(document.getElementById('editAge').value),
            email: document.getElementById('editEmail').value.trim(),
            username: document.getElementById('editUsername').value.trim(),
            password: document.getElementById('editPassword').value,
            roleIds: getSelectedRoleIds(editRolesSelect)
        };

        await fetchJson(API.users, {
            method: 'PUT',
            headers: buildHeaders(true),
            body: JSON.stringify(payload)
        });

        window.jQuery && window.jQuery('#editUserModal').modal('hide');
        await loadUsers();
    }

    async function openDeleteModal(userId) {
        hideError();
        const user = await fetchJson(`${API.users}/${userId}`, {
            method: 'GET',
            headers: buildHeaders(false)
        });

        deleteUserId = user.id;

        document.getElementById('deleteId').textContent = user.id;
        document.getElementById('deleteEmail').textContent = user.email ?? '';
        document.getElementById('deleteRoles').textContent = roleNames(user);

        window.jQuery && window.jQuery('#deleteUserModal').modal('show');
    }

    async function onDeleteConfirmed() {
        if (!deleteUserId) {
            return;
        }

        hideError();
        await fetchJson(`${API.users}/${deleteUserId}`, {
            method: 'DELETE',
            headers: buildHeaders(false)
        });

        deleteUserId = null;
        window.jQuery && window.jQuery('#deleteUserModal').modal('hide');
        await loadUsers();
    }

    function onTableClick(e) {
        const editBtn = e.target.closest('.btn-edit');
        const deleteBtn = e.target.closest('.btn-delete');
        if (editBtn) {
            const id = editBtn.getAttribute('data-id');
            openEditModal(id).catch(err => showError(err.message));
        }
        if (deleteBtn) {
            const id = deleteBtn.getAttribute('data-id');
            openDeleteModal(id).catch(err => showError(err.message));
        }
    }

    async function init() {
        try {
            await loadRoles();
            await loadUsers();
        } catch (e) {
            showError(e.message);
        }
    }

    if (newUserForm) {
        newUserForm.addEventListener('submit', (e) => {
            onCreateUser(e).catch(err => showError(err.message));
        });
    }

    if (editUserForm) {
        editUserForm.addEventListener('submit', (e) => {
            onEditUser(e).catch(err => showError(err.message));
        });
    }

    if (usersTableBody) {
        usersTableBody.addEventListener('click', onTableClick);
    }

    if (confirmDeleteBtn) {
        confirmDeleteBtn.addEventListener('click', () => {
            onDeleteConfirmed().catch(err => showError(err.message));
        });
    }

    document.addEventListener('DOMContentLoaded', init);
})();
