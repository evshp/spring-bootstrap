

// Показать модальное окно для создания пользователя
$('#userEditDialog').on('shown.bs.modal', function (event) {
    let button = $(event.relatedTarget)
    let userId = button.data('userid')
    let modal = $('#userEditDialog');
    if (userId) {
        modal.find('#user-error').hide()
        // Вызов модального окна для удаления пользователя
        if (button.data('action') === 'delete') {
            $.get({
                url: '/user/' + userId,
                success: (data) => {
                    modal.find('#userId').val(data.id)
                    modal.find('#user-name',  ).val(data.name).prop('readonly', true)
                    modal.find('#user-lastname').val(data.lastname).prop('readonly', true)
                    modal.find('#user-email').val(data.email).prop('readonly', true)
                    modal.find('#user-dateOfBirth').val(data.dateOfBirth).prop('readonly', true)
                    modal.find('#user-password-label').addClass('d-none')
                    modal.find('#user-password').val(data.password).addClass('d-none')
                    modal.find('#user-roles').val(data.roles).prop('disabled', true)
                    modal.find('#confirm-button').addClass('btn-danger')
                        .removeClass('btn-primary')
                        .text('Delete')
                    modal.find('#user-error').val('')
                },
                error: (err) => {
                    alert(err);
                },

            });
            // Подтверждение удаления пользователя
            $('#confirm-button').click(function () {
                let modal = $('#userEditDialog')
                let userId = $('#userId').val();

                console.log('Удаление пользователя с id: ' + userId)


                // Отправка запроса на удаление пользователя
                $.ajax({
                    url: '/user/delete/' + userId,
                    type: 'DELETE',
                    success: function () {
                        // Обработка успешного удаления пользователя
                        let userTable = document.getElementById('users-table');
                        let userRow = document.getElementById('user-' + userId);
                        if (userRow) {
                            userTable.deleteRow(userRow.rowIndex);
                            console.log('Пользователь успешно удален');
                        } else {
                            console.log('Пользователь не найден');
                        }
                    },
                    error: function (error) {
                        // Обработка ошибки удаления пользователя
                        console.log('Ошибка при удалении пользователя:', error);
                    },

                })
                modal.hide();
                $('.modal-backdrop').remove();

            });

        } else {
            // Вызов модального окна для редактирования пользователя
            $.get({
                url: '/user/' + userId,
                error: (err) => {
                    alert(err);
                },
                // Заполнение полей модального окна данными пользователя
                success: (data) => {
                    modal.find('#user-error').hide()
                    modal.find('#userId').val(data.id)
                    modal.find('#user-name').val(data.name).prop('readonly', false)
                    modal.find('#user-lastname').val(data.lastname).prop('readonly', false)
                    modal.find('#user-email').val(data.email).prop('readonly', false)
                    modal.find('#user-dateOfBirth').val(data.dateOfBirth).prop('readonly', false)
                    modal.find('#user-password-label').removeClass('d-none')
                    modal.find('#user-password').val(data.password).removeClass('d-none')
                    modal.find('#user-password').val(data.password).prop('readonly', false)
                    modal.find('#user-roles').val(data.roles).prop('disabled', false)
                    modal.find('#confirm-button').addClass('btn-primary')
                        .removeClass('btn-danger')
                        .text('Save')
                    modal.find('#user-error').val('');




                }
            });
            // Подтверждение редактирования пользователя
            $('#confirm-button').click(function () {
                let user = {
                    id: modal.find('#userId').val(),
                    name: modal.find('#user-name').val(),
                    lastname: modal.find('#user-lastname').val(),
                    email: modal.find('#user-email').val(),
                    dateOfBirth: modal.find('#user-dateOfBirth').val(),
                    password: modal.find('#user-password').val(),
                    roles: getRoles(modal,"#user-roles" )
                };
                // Отправка запроса на редактирование пользователя
                $.ajax({
                    url: '/user/edit',
                    type: 'POST',
                    data: JSON.stringify(user),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",

                    success: function (response) {
                        modal.hide()
                        $('.modal-backdrop').remove()
                    // Обработка успешного редактирования пользователя
                        $.get({
                            url: '/user/' + userId,
                            success: (data) => {
                                let userName = document.getElementById('name-' + userId);
                                userName.textContent = data.name;
                                let userLastname = document.getElementById('lastname-' + userId);
                                userLastname.textContent = data.lastname;
                                let userEmail = document.getElementById('email-' + userId);
                                userEmail.textContent = data.email;
                                let userAge = document.getElementById('age-' + userId);
                                userAge.textContent = getAgeFromDateOfBirth(data.dateOfBirth);
                                let userRoles = document.getElementById('role-' + userId);
                                let userRole = data.roles
                                const shortenedNames = userRole.map(role => role.name.substring(5));
                                userRoles.innerText = shortenedNames.join(' ');
                                console.log('Пользователь успешно отредактирован');


                            },
                            error: (err) => {
                                alert(err);
                            },

                        });
                    },
                    error: function (xhr) {
                        if (xhr.status === 400) {
                            console.log(xhr.responseText)
                            modal.find('#user-error').text(xhr.responseText).css('color', 'red').show();
                        }
                    }

                })


            });
        }
    }
})


$('#createForm').submit(function(event) {
    event.preventDefault(); // отменяем действие по умолчанию (отправку формы)
    // ваш скрипт здесь
    let user = {
        name: $('#name').val(),
        lastname: $('#lastname').val(),
        email: $('#email').val(),
        dateOfBirth: $('#dateOfBirth').val(),
        password: $('#password').val(),
        roles: getRoles($('#createForm'), "#roles" )
    };

        $.ajax({
            url: '/user/create',
            type: 'POST',
            data: JSON.stringify(user),
            contentType: "application/json; charset=utf-8",
            dataType: "json",

            success: function () {
                $('#user-create-error').hide();
                location.reload();
                console.log('Пользователь успешно создан');

            },

            error: function (xhr) {
                console.log(xhr.responseText)
                if (xhr.status === 400) {
                    $('#user-create-error').text(xhr.responseText).css('color', 'red').show();
                }
            }
    })


});






//Рассчет возраста из даты рождения
function getAgeFromDateOfBirth(dateOfBirth) {
    var today = new Date();
    var birthDate = new Date(dateOfBirth);
    var age = today.getFullYear() - birthDate.getFullYear();
    var monthDiff = today.getMonth() - birthDate.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }
    return age;
}

//Получение ролей
function getRoles(modal, id) {
    let collection = [];
    let temp = modal.find(id).val();

    temp.forEach(function(value) {
        let name = "";
        if(value === "1") {
            name = "ROLE_ADMIN";
        } else if(value === "2") {
            name = "ROLE_USER";
        }
        let role = {
            id: value,
            name: name
        };
        collection.push(role);
    });
    return collection;
}











