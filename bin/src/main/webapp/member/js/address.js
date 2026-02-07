document.addEventListener("DOMContentLoaded", function () {

    const address = document.getElementById('address');
    const readonlyFields = document.querySelector('.member-form .readonly');

    address.addEventListener("click", function (e) {
        e.preventDefault();

        fetch('address')
            .then(result => result.json())
            .then(member => {
                console.log(member);
                readonlyFields.textContent = member.address;
               
            })

    });

});