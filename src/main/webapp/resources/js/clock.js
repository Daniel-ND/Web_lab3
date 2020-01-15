function clock() {
    var date = new Date();
    document.getElementById('date').innerHTML = date.toLocaleDateString();
    document.getElementById('clock').innerHTML = date.toLocaleTimeString();
    window.setTimeout('clock()',12000);
}

window.onload = function () {
    clock();
};
