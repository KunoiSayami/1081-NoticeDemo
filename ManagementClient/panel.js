var submit_button = document.getElementById('firebase_send');
function init_panel() {
    submit_button.addEventListener('click', async _ => {
        try {
            const response = await fetch('/request.php', {
                method: 'post',
                body: {

                }
            });
        } catch (err) {
            console.error(`Error: ${err}`);
        }
    });
}