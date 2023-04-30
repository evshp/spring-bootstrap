$('#userEditDialog').on('shown.bs.modal', function (event) {
    let button = $(event.relatedTarget) // Button that triggered the modal
    let userId = button.data('userid') // Extract info from data-* attributes

    if (userId) {
        console.log(button.data('action'));
        if (button.data('action') === 'delete') {
            $.get({
                url: '/user/' + userId,
                success: (data) => {
                    let modal = $(this)
                    modal.find('#user-error').hide()
                    modal.find('#userId').val(data.id)
                    modal.find('#user-name').val(data.name).prop('readonly', true)
                    modal.find('#user-lastname').val(data.lastname).prop('readonly', true)
                    modal.find('#user-email').val(data.email).prop('readonly', true)
                    modal.find('#user-dateOfBirth').val(data.dateOfBirth).prop('readonly', true)
                    modal.find('#user-password-label').addClass('d-none')
                    modal.find('#user-password').val(data.password).addClass('d-none')
                    modal.find('#user-roles').val(data.roles).prop('disabled', true)
                    modal.find('#save-user-button').addClass('btn-danger')
                        .removeClass('btn-primary')
                        .text('Delete')
                    modal.find('#user-error').val('')
                },
                error: (err) => {
                    alert(err);
                },

            });

            $('#save-user-button').click(function () {

                let modal = $('#userEditDialog')
                let userId = $('#userId').val();

                $.ajax({
                    url: '/user/delete/' + userId,
                    type: 'DELETE',
                    success: function () {
                        // Обработка успешного удаления пользователя
                        console.log('Пользователь успешно удален');
                        location.reload();
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
            $.get({
                url: '/user/' + userId,
                error: (err) => {
                    alert(err);
                },

                success: (data) => {

                    let modal = $(this)
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
                    modal.find('#save-user-button').addClass('btn-primary')
                        .removeClass('btn-danger')
                        .text('Save')
                    modal.find('#user-error').val('')


                }
            });

            $('#save-user-button').click(function () {

                let modal = $('#userEditDialog')
                let collection = [];
                let role
                let temp = []
                temp = modal.find("#user-roles").val()
                for (let i = 0; i < temp.length; i++) {
                    collection.push(role = {
                        id: temp[i]

                    })
                }


                let user = {
                    id: modal.find('#userId').val(),
                    name: modal.find('#user-name').val(),
                    lastname: modal.find('#user-lastname').val(),
                    email: modal.find('#user-email').val(),
                    dateOfBirth: modal.find('#user-dateOfBirth').val(),
                    password: modal.find('#user-password').val(),
                    roles: collection


                };


                $.ajax({
                    url: '/user/edit',
                    type: 'POST',
                    data: JSON.stringify(user),
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",

                    success: function (response) {
                        modal.hide()
                        $('.modal-backdrop').remove()


                    },
                    error: function (xhr, status, error) {
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







