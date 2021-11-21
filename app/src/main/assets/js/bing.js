Element.prototype.remove = function () {
    this.parentElement.removeChild(this);
}
NodeList.prototype.remove = HTMLCollection.prototype.remove = function () {
    for (var i = this.length - 1; i >= 0; i--) {
        if (this[i] && this[i].parentElement) {
            this[i].parentElement.removeChild(this[i]);
        }
    }
}

elementIds = ["b_footer", "b_tween", "b_header", "b_context", "est_switch"] // Elements need to removed by id
try {
    elementIds.forEach(element => document.getElementById(element).remove())
} catch (err) {
    console.log(err)
}

elementClasses = ["b_ad b_adTop", "b_ad b_adBottom", "vsarr1stbig vasac", "b_ans", "b_scopebar", "b_logoArea", "b_msg b_canvas", "b_pag"] // Elements need to removed by class
for (let i = 0; i < elementClasses.length; i++) {
    try {
        document.getElementsByClassName(elementClasses[i]).remove()
    } catch (err) {
        console.log(err)
    }
}