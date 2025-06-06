
document.addEventListener('DOMContentLoaded', () => {
    marked.setOptions({ headerIds: true });
    fetch('Dokumentation.md')
        .then(resp => resp.text())
        .then(markdown => {
            const html = marked.parse(markdown);
            document.getElementById('content').innerHTML = html;
            buildTree();
        });

    const searchInput = document.getElementById('search');
    searchInput.addEventListener('input', () => {
        const q = searchInput.value.toLowerCase();
        document.querySelectorAll('#content p, #content li').forEach(el => {
            const text = el.textContent.toLowerCase();
            if (q && text.includes(q)) el.classList.add('highlight');
            else el.classList.remove('highlight');
        });
    });
});

function buildTree() {
    const toc = document.getElementById('toc');
    const stack = [{ level: 0, ul: toc }];
    document.querySelectorAll('#content h1, #content h2, #content h3').forEach(h => {
        const level = parseInt(h.tagName.substring(1));
        const li = document.createElement('li');
        const a = document.createElement('a');
        a.textContent = h.textContent;
        a.href = '#' + h.id;
        li.appendChild(a);
        while (stack.length > level) stack.pop();
        let parent = stack[stack.length - 1];
        if (!parent.ul) {
            parent.ul = document.createElement('ul');
            parent.li.appendChild(parent.ul);
        }
        parent.ul.appendChild(li);
        stack.push({ level: level, li: li });
    });
    document.querySelectorAll('#toc li').forEach(li => {
        if (li.querySelector('ul')) {
            li.classList.add('collapsed');
            li.addEventListener('click', e => {
                if (e.target.tagName !== 'A') {
                    li.classList.toggle('collapsed');
                }
            });
        }
    });
}

