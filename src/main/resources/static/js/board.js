window.onload = function () {
    loadLikeCount();
    loadComments();
};

function loadLikeCount() {
    let id = document.getElementById('board-id').value;
    getLikeCount(id, function (likeCount) {
        document.getElementById('like-count').innerText = likeCount;
    });
}

function loadComments() {
    let id = document.getElementById('board-id').value;

    fetch(`/api/boards/${id}/comments`, {
        method: 'GET',
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            updateCommentList(data);
        })
        .catch(error => console.error('댓글 가져오기 오류:', error));
}



const deleteButton = document.getElementById('delete-btn');

if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('board-id').value;
        function success() {
            alert('삭제가 완료되었습니다.');
            location.replace('/boards');
        }

        function fail() {
            alert('삭제 실패했습니다.');
            location.replace('/boards');
        }

        httpRequest('DELETE',`/api/boards/${id}`, null, success, fail);
    });
}

const modifyButton = document.getElementById('modify-btn');

if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        })

        function success() {
            alert('수정 완료되었습니다.');
            location.replace(`/boards/${id}`);
        }

        function fail() {
            alert('수정 실패했습니다.');
            location.replace(`/boards/${id}`);
        }

        httpRequest('PUT',`/api/boards/${id}`, body, success, fail);
    });
}

const createButton = document.getElementById('create-btn');

if (createButton) {
    createButton.addEventListener('click', event => {
        body = JSON.stringify({
            title: document.getElementById('title').value,
            content: document.getElementById('content').value
        });
        function success() {
            alert('등록 완료되었습니다.');
            location.replace('/boards');
        };
        function fail() {
            alert('등록 실패했습니다.');
            location.replace('/boards');
        };

        httpRequest('POST','/api/boards', body, success, fail)
    });
}


function getCookie(key) {
    var result = null;
    var cookie = document.cookie.split(';');
    cookie.some(function (item) {
        item = item.replace(' ', '');

        var dic = item.split('=');

        if (key === dic[0]) {
            result = dic[1];
            return true;
        }
    });

    return result;
}

function httpRequest(method, url, body, success, fail) {
    fetch(url, {
        method: method,
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        },
        body: body,
    }).then(response => {
        if (response.status === 200 || response.status === 201) {
            return success();
        }
        const refresh_token = getCookie('refresh_token');
        if (response.status === 401 && refresh_token) {
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('access_token'),
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: getCookie('refresh_token'),
                }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    }
                })
                .then(result => {
                    localStorage.setItem('access_token', result.accessToken);
                    httpRequest(method, url, body, success, fail);
                })
                .catch(error => fail());
        } else {
            return fail();
        }
    });
}

function updateCommentList(comments) {
    let commentListDiv = document.getElementById('comment-list');
    commentListDiv.innerHTML = '';
    comments.forEach(comment => {
        const commentDiv = document.createElement('div');
        const formattedDate = new Date(comment.createdAt).toISOString().slice(0, 16).replace('T', ' ');
        commentDiv.innerHTML = `
            <p><strong>${comment.userName}</strong>: ${comment.content}</p>
            <p>${formattedDate}</p>
            <div>
                <button class="btn btn-outline-primary btn-sm" onclick="updateComment(${comment.id}, prompt('댓글을 수정하세요', '${comment.content}'));">수정</button>
                <button class="btn btn-outline-secondary btn-sm ml-2" onclick="deleteComment(${comment.id});">삭제</button>
            </div>
        `;
        commentListDiv.appendChild(commentDiv);
    });
}





const addCommentButton = document.getElementById('add-comment-btn');
if (addCommentButton) {
    addCommentButton.addEventListener('click', event => {
        let id = document.getElementById('board-id').value;
        const body = JSON.stringify({
            content: document.getElementById('comment-input').value,
        });

        function success() {
            alert('댓글이 추가되었습니다!');
            loadComments();
            document.getElementById('comment-input').value = '';
        }

        function fail() {
            alert('댓글 추가에 실패했습니다!');
        }

        httpRequest('POST', `/api/boards/${id}/comments`, body, success, fail);
    });
}

const likeButton = document.getElementById('like-btn');
let isLiked = false;

likeButton.addEventListener('click', event => {
    let id = document.getElementById('board-id').value;
    const body = JSON.stringify({
        boardId: id
    });

    function updateLikeCount(likeCount) {
        document.getElementById('like-count').innerText = likeCount;
    }

    function success() {
        isLiked = !isLiked;
        likeButton.innerText = isLiked ? 'Liked' : 'Like';
        getLikeCount(id, updateLikeCount);
    }

    function fail() {
        alert('Failed to like this post!');
    }

    httpRequest('POST', `/api/boards/${id}/likes`, body, success, fail);
});

function getLikeCount(id, success) {
    fetch(`/api/boards/${id}/likeCount`, {
        method: 'GET',
        headers: {
            Authorization: 'Bearer ' + localStorage.getItem('access_token'),
            'Content-Type': 'application/json',
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            success(data);
        })
        .catch(error => console.error('Error fetching like count:', error));
}

function updateComment(commentId, newContent) {
    const url = `/api/comments/${commentId}`;
    const body = JSON.stringify({ content: newContent });

    function success() {
        alert('댓글이 수정되었습니다.');
        loadComments();
    }

    function fail() {
        alert('댓글 수정에 실패했습니다.');
    }

    httpRequest('PUT', url, body, success, fail);
}

function deleteComment(commentId) {
    const url = `/api/comments/${commentId}`;

    function success() {
        alert('댓글이 삭제되었습니다.');
        loadComments();
    }

    function fail() {
        alert('댓글 삭제에 실패했습니다.');
    }

    httpRequest('DELETE', url, null, success, fail);
}


