import threading

from flask import Flask, send_file
import nms_handler

app = Flask(__name__)
app.config["DEBUG"] = True

handler = nms_handler.NmsHandler()


@app.route("/")
def do():
    if handler.updating:
        return "<h1>The Jar Is Currently Updating...</h1>"

    if nms_handler.needs_update() or handler.jarPath is None:
        threading.Thread(target=handler.init).start()
        return "<h1>The Jar Started Updating..</h1>"

    if handler.jarPath is not None:
        return send_file(handler.jarPath, as_attachment=True)

    return "<h1>The Jar Path Is Not Found</h1>"


if __name__ == "__main__":
    app.run(port=2505)
