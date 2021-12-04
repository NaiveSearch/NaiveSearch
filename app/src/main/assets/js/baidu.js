var ads = 0;
Element.prototype.remove = function () {
    this.parentElement.removeChild(this);
    ads++;
}
NodeList.prototype.remove = HTMLCollection.prototype.remove = function () {
    for (var i = this.length - 1; i >= 0; i--) {
        if (this[i] && this[i].parentElement) {
            this[i].parentElement.removeChild(this[i]);
            ads++;
        }
    }
}

elementIds = ["content_right", "head", "s_tab","top-ad"] // Elements need to removed by id
try {
    elementIds.forEach(element => document.getElementById(element).remove())
} catch (err) {
    console.log(err)
}

elementClasses = ["head_nums_cont_outer", "result-molecule", "b2c-universal-card", "b2c-universal-card-footer"] // Elements need to removed by class
for (let i = 0; i < elementClasses.length; i++) {
    try {
        document.getElementsByClassName(elementClasses[i]).remove()
    } catch (err) {
        console.log(err)
    }
}

(function () {
    $('#content_left>div').not('.c-container').css({
            height: 0,
            overflow: 'hidden',
        },
        ads++
    )
})();
(function (){
   window.location.href="naivesearch://statistics?ads="+ ads;
})();
