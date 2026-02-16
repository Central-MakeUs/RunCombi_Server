function adminRequest(options) {
    const method = options.method || "GET";
    const headers = Object.assign({}, options.headers || {});
    const fetchOptions = {
        method: method,
        headers: headers
    };

    if (options.data !== undefined && options.data !== null) {
        fetchOptions.body = JSON.stringify(options.data);
        if (!headers["Content-Type"]) {
            headers["Content-Type"] = "application/json";
        }
    }

    fetch(options.url, fetchOptions)
        .then(async response => {
            let body = null;
            try {
                body = await response.json();
            } catch (ignore) {
                body = null;
            }

            if (response.ok) {
                if (typeof options.onSuccess === "function") {
                    options.onSuccess(body, response);
                }
                return;
            }

            if (typeof options.onError === "function") {
                options.onError(body, response);
            }
        })
        .catch(error => {
            if (typeof options.onNetworkError === "function") {
                options.onNetworkError(error);
            }
        })
        .finally(() => {
            if (typeof options.onFinally === "function") {
                options.onFinally();
            }
        });
}

function adminGetJson(url, callbacks) {
    adminRequest(Object.assign({}, callbacks || {}, {
        url: url,
        method: "GET"
    }));
}

function adminPostJson(url, data, callbacks) {
    adminRequest(Object.assign({}, callbacks || {}, {
        url: url,
        method: "POST",
        data: data
    }));
}

window.adminRequest = adminRequest;
window.adminGetJson = adminGetJson;
window.adminPostJson = adminPostJson;
