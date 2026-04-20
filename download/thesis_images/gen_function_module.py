import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as patches
from matplotlib.font_manager import fontManager

# ── Font setup ──
fontManager.addfont('/usr/share/fonts/truetype/chinese/SimHei.ttf')
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# ── Figure setup ──
fig_w, fig_h = 18, 12
fig, ax = plt.subplots(figsize=(fig_w, fig_h), dpi=150)
fig.patch.set_facecolor('white')
ax.set_facecolor('white')
ax.set_xlim(0.5, fig_w - 0.5)
ax.set_ylim(1.0, fig_h - 0.5)
ax.axis('off')

# ── Constants ──
BOX_H = 0.50          # box height in inches
PAD = 0.06            # FancyBboxPatch round-corner pad
CHILD_DY = 0.78       # vertical spacing between child centres

# ── Font sizes ──
FS_ROOT = 16
FS_L1 = 15
FS_L2 = 14

# ── Helper: box width based on text length ──
def box_width(text: str) -> float:
    n = len(text)
    if n >= 7:
        return 2.8
    elif n >= 6:
        return 2.5
    else:
        return 2.0

# ── Helper: draw one rounded box with centred text ──
def draw_box(cx, cy, w, h, text, fs):
    x = cx - w / 2
    y = cy - h / 2
    rect = patches.FancyBboxPatch(
        (x, y), w, h,
        boxstyle=f"round,pad={PAD}",
        linewidth=1.5,
        edgecolor='black',
        facecolor='white',
    )
    ax.add_patch(rect)
    ax.text(cx, cy, text,
            ha='center', va='center',
            fontsize=fs, color='black')

# ── Helper: draw a straight line ──
def line(x1, y1, x2, y2):
    ax.plot([x1, x2], [y1, y2], color='black', linewidth=1.2)

# ── Tree data ──
root_text = '校园兼职平台系统'

l1_nodes = [
    ('管理员端', 3.0),
    ('雇主端',  9.0),
    ('学生端', 15.0),
]

l2_groups = {
    '管理员端': ['登录认证', '数据统计', '兼职审核', '用户管理', '数据字典管理'],
    '雇主端':  ['登录注册', '发布兼职', '我的兼职管理', '申请管理', '订单结算'],
    '学生端':  ['登录注册', '兼职浏览搜索', '兼职详情与申请',
                '我的申请', '我的订单', '评价管理', '个人信息管理'],
}

# ── Y positions ──
ROOT_Y = 11.0
L1_Y   = 9.2
L2_Y0  = 7.8          # centre of first child row

# ── Draw ROOT ──
ROOT_X = fig_w / 2    # 9.0
draw_box(ROOT_X, ROOT_Y, 3.5, BOX_H, root_text, FS_ROOT)

# ── Draw Level-1 nodes and root-to-L1 lines ──
for text, x in l1_nodes:
    draw_box(x, L1_Y, 2.2, BOX_H, text, FS_L1)
    line(ROOT_X, ROOT_Y - BOX_H / 2, x, L1_Y + BOX_H / 2)

# ── Draw Level-2 children with bracket lines ──
for parent_text, parent_x in l1_nodes:
    children = l2_groups[parent_text]
    n = len(children)

    # Compute child centre-y positions (top to bottom)
    child_cys = [L2_Y0 - i * CHILD_DY for i in range(n)]

    # Draw vertical "trunk" line from parent bottom to last child top
    trunk_top = L1_Y - BOX_H / 2
    trunk_bot = child_cys[-1] + BOX_H / 2
    line(parent_x, trunk_top, parent_x, trunk_bot)

    # Draw each child box (on top of trunk → white fill hides the line inside box)
    for i, ctext in enumerate(children):
        cy = child_cys[i]
        w = box_width(ctext)
        draw_box(parent_x, cy, w, BOX_H, ctext, FS_L2)

# ── Final checks (printed to stdout) ──
print("=== Self-check ===")
for parent_text, parent_x in l1_nodes:
    children = l2_groups[parent_text]
    child_cys = [L2_Y0 - i * CHILD_DY for i in range(len(children))]
    for i, ct in enumerate(children):
        w = box_width(ct)
        cx, cy = parent_x, child_cys[i]
        left  = cx - w/2 - PAD
        right = cx + w/2 + PAD
        bot   = cy - BOX_H/2 - PAD
        top   = cy + BOX_H/2 + PAD
        # Text width estimate (conservative: 14pt CJK ≈ 0.197 in/char)
        tw = len(ct) * 0.197
        side_pad = (w - tw) / 2
        assert side_pad >= 0.15, f"Side padding too small for '{ct}': {side_pad:.3f}"
        assert bot >= 1.0, f"'{ct}' bottom {bot:.2f} below ylim"
        assert top <= fig_h - 0.5, f"'{ct}' top {top:.2f} above ylim"
        assert left >= 0.5, f"'{ct}' left {left:.2f} left of xlim"
        assert right <= fig_w - 0.5, f"'{ct}' right {right:.2f} right of xlim"
print("All checks passed!")

# ── Save ──
plt.tight_layout(pad=2.0)
save_path = '/home/z/my-project/Xiao-Yi-Pin/download/thesis_images/function_module_diagram.png'
fig.savefig(save_path,
            dpi=150,
            bbox_inches='tight',
            facecolor='white',
            edgecolor='none')
plt.close()
print(f"Saved to {save_path}")
