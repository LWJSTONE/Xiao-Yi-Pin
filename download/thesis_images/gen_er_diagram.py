#!/usr/bin/env python3
"""Regenerate E-R Diagram for campus part-time job platform — Black & White only."""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.font_manager import fontManager
from matplotlib.patches import FancyBboxPatch

# ── Font setup ──────────────────────────────────────────────────────────────
fontManager.addfont('/usr/share/fonts/truetype/chinese/SimHei.ttf')
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# ── Style constants ─────────────────────────────────────────────────────────
ATTR_FONT  = 10
NAME_FONT  = 13
LINE_W     = 1.5
BOX_W      = 4.5          # wide enough for longest text
REL_LW     = 1.0
HEADER_H   = 0.55         # header section height
ATTR_ROW_H = 0.40         # height per attribute row
PAD_BOTTOM = 0.60         # extra padding at bottom
PAD_TOP    = 0.20         # extra padding at top
PAD_X      = 0.35         # horizontal text padding inside box

# ── Figure ──────────────────────────────────────────────────────────────────
fig, ax = plt.subplots(figsize=(24, 18), dpi=150)
ax.set_facecolor('white')
fig.patch.set_facecolor('white')
ax.set_xlim(0, 22)
ax.set_ylim(0, 18)
ax.set_aspect('equal')
ax.axis('off')

# ── Entity definitions ──────────────────────────────────────────────────────
entities_data = {
    '系统用户': {
        'name': '系统用户 (SysUser)',
        'attrs': [
            '用户ID (id)',
            '用户名 (username)',
            '密码哈希 (password_hash)',
            '手机号 (phone)',
            '邮箱 (email)',
            '角色类型 (role_type)',
            '状态 (status)',
            '创建时间 (create_time)',
        ],
        'cx': 3, 'cy': 15,
    },
    '兼职分类': {
        'name': '兼职分类 (JobCategory)',
        'attrs': [
            '分类ID (id)',
            '分类名称 (name)',
            '父级ID (parent_id)',
            '排序 (sort_order)',
            '状态 (status)',
        ],
        'cx': 10, 'cy': 15,
    },
    '系统字典': {
        'name': '系统字典 (SysDict)',
        'attrs': [
            '字典ID (id)',
            '字典编码 (dict_code)',
            '字典标签 (dict_label)',
            '字典值 (dict_value)',
            '排序 (sort_order)',
            '状态 (status)',
        ],
        'cx': 18, 'cy': 15,
    },
    '用户档案': {
        'name': '用户档案 (UserProfile)',
        'attrs': [
            '档案ID (id)',
            '用户ID (user_id)',
            '真实姓名 (real_name)',
            '性别 (gender)',
            '学校 (university)',
            '专业 (major)',
            '年级 (grade)',
            '账户余额 (balance)',
            '信用积分 (credit_score)',
            '认证状态 (verified_status)',
        ],
        'cx': 3, 'cy': 9,
    },
    '兼职信息': {
        'name': '兼职信息 (JobPost)',
        'attrs': [
            '兼职ID (id)',
            '发布者ID (publisher_id)',
            '分类ID (category_id)',
            '岗位名称 (title)',
            '工作描述 (description)',
            '工作地点 (location)',
            '薪资类型 (salary_type)',
            '薪资金额 (salary_amount)',
            '招聘人数 (recruit_num)',
            '审核状态 (audit_status)',
        ],
        'cx': 10, 'cy': 9,
    },
    '求职申请': {
        'name': '求职申请 (Application)',
        'attrs': [
            '申请ID (id)',
            '兼职ID (job_id)',
            '申请者ID (applicant_id)',
            '申请时间 (apply_time)',
            '申请状态 (status)',
            '审核备注 (review_remark)',
        ],
        'cx': 3, 'cy': 3,
    },
    '工作订单': {
        'name': '工作订单 (OrderRecord)',
        'attrs': [
            '订单ID (id)',
            '申请ID (application_id)',
            '学生ID (student_id)',
            '雇主ID (employer_id)',
            '订单金额 (amount)',
            '结算状态 (settle_status)',
            '结算时间 (settle_time)',
        ],
        'cx': 10, 'cy': 3,
    },
    '评价信息': {
        'name': '评价信息 (Review)',
        'attrs': [
            '评价ID (id)',
            '订单ID (order_id)',
            '评价者ID (reviewer_id)',
            '被评价者ID (reviewee_id)',
            '评分 (rating)',
            '评论内容 (comment)',
        ],
        'cx': 18, 'cy': 3,
    },
}


# ── Draw entity box ─────────────────────────────────────────────────────────
def draw_entity(cx, cy, name, attributes):
    """
    Draw an entity box centered at (cx, cy).
    Returns dict with bounding box and anchor points.
    """
    n = len(attributes)
    # Box height = header + attributes + padding
    body_h = n * ATTR_ROW_H
    box_h = HEADER_H + body_h + PAD_TOP + PAD_BOTTOM
    box_w = BOX_W

    x0 = cx - box_w / 2   # left edge
    y0 = cy - box_h / 2   # bottom edge

    # Rectangle
    rect = FancyBboxPatch(
        (x0, y0), box_w, box_h,
        boxstyle="round,pad=0.1",
        linewidth=LINE_W, edgecolor='black', facecolor='white', zorder=3,
    )
    ax.add_patch(rect)

    # Header separator line
    sep_y = y0 + box_h - PAD_TOP - HEADER_H
    ax.plot([x0 + 0.08, x0 + box_w - 0.08], [sep_y, sep_y],
            color='black', linewidth=1.0, zorder=4)

    # Entity name text (centered in header)
    name_y = (sep_y + y0 + box_h) / 2
    ax.text(cx, name_y, name,
            fontsize=NAME_FONT, fontweight='bold',
            ha='center', va='center', color='black', zorder=5)

    # Attribute texts
    first_attr_y = sep_y - ATTR_ROW_H / 2
    for i, attr in enumerate(attributes):
        ay = first_attr_y - i * ATTR_ROW_H
        is_pk = (i == 0)
        ax.text(x0 + PAD_X, ay, attr,
                fontsize=ATTR_FONT, ha='left', va='center',
                color='black', zorder=5,
                fontweight='bold' if is_pk else 'normal')

        # Underline primary key
        if is_pk:
            # Use figure canvas renderer for text extent
            t = ax.text(x0 + PAD_X, ay, attr,
                        fontsize=ATTR_FONT, ha='left', va='center',
                        color='black', zorder=5, fontweight='bold')
            bb = t.get_window_extent(renderer=fig.canvas.get_renderer())
            inv = ax.transData.inverted()
            p0 = inv.transform((bb.x0, bb.y0))
            p1 = inv.transform((bb.x1, bb.y1))
            t.remove()
            # Redraw the text
            ax.text(x0 + PAD_X, ay, attr,
                    fontsize=ATTR_FONT, ha='left', va='center',
                    color='black', zorder=5, fontweight='bold')
            # Underline
            ax.plot([p0[0], p1[0]],
                    [p0[1] - 0.03, p0[1] - 0.03],
                    color='black', linewidth=0.8, zorder=6)

    return {
        'cx': cx, 'cy': cy,
        'left': x0, 'right': x0 + box_w,
        'top': y0 + box_h, 'bottom': y0,
        'left_cy': cy, 'right_cy': cy,
        'top_cx': cx, 'bottom_cx': cx,
        'box_h': box_h, 'box_w': box_w,
    }


# ── Helper: label with white background ─────────────────────────────────────
def _label(txt, lx, ly, fontsize=10):
    ax.text(lx, ly, txt, fontsize=fontsize, fontweight='bold',
            ha='center', va='center', color='black',
            bbox=dict(boxstyle='round,pad=0.15', fc='white', ec='none'),
            zorder=7)


def _rel_label(txt, lx, ly, fontsize=9):
    ax.text(lx, ly, txt, fontsize=fontsize, fontstyle='italic',
            ha='center', va='center', color='black',
            bbox=dict(boxstyle='round,pad=0.12', fc='white', ec='none'),
            zorder=7)


# ── Draw relationship (straight or routed) ──────────────────────────────────
def draw_rel(e1, side1, e2, side2, l1='1', l2='n', label='',
             waypoints=None):
    """Connect two entities. waypoints is a list of (x,y) for polyline routing."""
    pts_map = {
        'bottom': lambda e: (e['bottom_cx'], e['bottom']),
        'top':    lambda e: (e['top_cx'],    e['top']),
        'left':   lambda e: (e['left'],      e['left_cy']),
        'right':  lambda e: (e['right'],     e['right_cy']),
    }
    p1 = pts_map[side1](e1)
    p2 = pts_map[side2](e2)

    if waypoints:
        path = [p1] + waypoints + [p2]
    else:
        path = [p1, p2]

    xs = [p[0] for p in path]
    ys = [p[1] for p in path]
    ax.plot(xs, ys, color='black', linewidth=REL_LW, zorder=2)

    # Cardinality labels near each end
    dx = p2[0] - p1[0]
    dy = p2[1] - p1[1]
    dist = max((dx**2 + dy**2)**0.5, 1e-6)
    ux, uy = dx / dist, dy / dist
    off = 0.45
    _label(l1, p1[0] + ux * off, p1[1] + uy * off)
    _label(l2, p2[0] - ux * off, p2[1] - uy * off)

    # Relationship name at midpoint
    if label:
        mx = (p1[0] + p2[0]) / 2
        my = (p1[1] + p2[1]) / 2
        px, py = -uy, ux  # perpendicular
        _rel_label(label, mx + px * 0.35, my + py * 0.35)


def draw_rel_routed(e1, side1, e2, side2, l1='1', l2='n', label='',
                    waypoints=None, label_pos=None, label_l1_off=None, label_l2_off=None):
    """Connect two entities via polyline waypoints. Allows custom label positions."""
    pts_map = {
        'bottom': lambda e: (e['bottom_cx'], e['bottom']),
        'top':    lambda e: (e['top_cx'],    e['top']),
        'left':   lambda e: (e['left'],      e['left_cy']),
        'right':  lambda e: (e['right'],     e['right_cy']),
    }
    p1 = pts_map[side1](e1)
    p2 = pts_map[side2](e2)

    if waypoints:
        path = [p1] + waypoints + [p2]
    else:
        path = [p1, p2]

    xs = [p[0] for p in path]
    ys = [p[1] for p in path]
    ax.plot(xs, ys, color='black', linewidth=REL_LW, zorder=2)

    # Cardinality labels
    if label_l1_off:
        _label(l1, label_l1_off[0], label_l1_off[1])
    else:
        dx = p2[0] - p1[0]
        dy = p2[1] - p1[1]
        dist = max((dx**2 + dy**2)**0.5, 1e-6)
        ux, uy = dx / dist, dy / dist
        off = 0.45
        _label(l1, p1[0] + ux * off, p1[1] + uy * off)

    if label_l2_off:
        _label(l2, label_l2_off[0], label_l2_off[1])
    else:
        dx = p2[0] - p1[0]
        dy = p2[1] - p1[1]
        dist = max((dx**2 + dy**2)**0.5, 1e-6)
        ux, uy = dx / dist, dy / dist
        off = 0.45
        _label(l2, p2[0] - ux * off, p2[1] - uy * off)

    # Relationship name
    if label:
        if label_pos:
            _rel_label(label, label_pos[0], label_pos[1])
        else:
            mx = (p1[0] + p2[0]) / 2
            my = (p1[1] + p2[1]) / 2
            _rel_label(label, mx, my)


# ═══════════════════════════════════════════════════════════════════════════
#  DRAW ENTITIES
# ═══════════════════════════════════════════════════════════════════════════
E = {}
for key, data in entities_data.items():
    E[key] = draw_entity(data['cx'], data['cy'], data['name'], data['attrs'])

# ═══════════════════════════════════════════════════════════════════════════
#  OVERLAP CHECK
# ═══════════════════════════════════════════════════════════════════════════
print("=== Overlap Check ===")
names = list(E.keys())
overlap_found = False
for i in range(len(names)):
    for j in range(i + 1, len(names)):
        a, b = E[names[i]], E[names[j]]
        # Add small margin for rounded corners
        margin = 0.15
        if (a['left'] - margin < b['right'] + margin and
            a['right'] + margin > b['left'] - margin and
            a['bottom'] - margin < b['top'] + margin and
            a['top'] + margin > b['bottom'] - margin):
            print(f"  OVERLAP: {names[i]} <-> {names[j]}")
            overlap_found = True
if not overlap_found:
    print("  No overlaps detected. All entity boxes are clear.")

# ═══════════════════════════════════════════════════════════════════════════
#  ENTITY COUNT CHECK
# ═══════════════════════════════════════════════════════════════════════════
print(f"\n=== Entity Count: {len(E)} (expected 8) ===")
for key in E:
    print(f"  {key}: box_h={E[key]['box_h']:.2f}")

# ═══════════════════════════════════════════════════════════════════════════
#  ATTRIBUTES CHECK
# ═══════════════════════════════════════════════════════════════════════════
expected_attrs = {
    '系统用户': 8, '用户档案': 10, '兼职分类': 5, '兼职信息': 10,
    '求职申请': 6, '工作订单': 7, '评价信息': 6, '系统字典': 6,
}
print("\n=== Attributes Check ===")
for key, exp_count in expected_attrs.items():
    actual = len(entities_data[key]['attrs'])
    status = "OK" if actual == exp_count else "MISMATCH"
    print(f"  {key}: {actual}/{exp_count} [{status}]")

# ═══════════════════════════════════════════════════════════════════════════
#  RELATIONSHIPS
# ═══════════════════════════════════════════════════════════════════════════

# 1. 系统用户 → 用户档案: 1—1 (拥有)  — vertical, bottom→top
draw_rel(E['系统用户'], 'bottom', E['用户档案'], 'top',
         l1='1', l2='1', label='拥有')

# 2. 系统用户 → 兼职信息: 1—n (发布)
#    Route: right of 系统用户 → right to x=6.5 → down to y=9
#           (clear corridor between columns, no entities at x=6.5)
#           → right to left of 兼职信息 (7.75, 9)
sys_right = (E['系统用户']['right'], E['系统用户']['right_cy'])
job_left = (E['兼职信息']['left'], E['兼职信息']['left_cy'])
draw_rel_routed(
    E['系统用户'], 'right', E['兼职信息'], 'left',
    l1='1', l2='n', label='发布',
    waypoints=[(6.5, sys_right[1]), (6.5, job_left[1])],
    label_l1_off=(sys_right[0] + 0.5, sys_right[1] + 0.30),
    label_l2_off=(job_left[0] - 0.45, job_left[1] + 0.30),
    label_pos=(6.5, (sys_right[1] + job_left[1]) / 2),
)

# 3. 系统用户 → 求职申请: 1—n (提交)
#    Route: left of 系统用户 → left to x=0.2 → down to y=4.88
#           → right to top of 求职申请 (avoids 用户档案 at x=[0.75, 5.25])
app_top = (E['求职申请']['top_cx'], E['求职申请']['top'])
route_x = 0.2  # left of all entities (leftmost edge = 0.75)
draw_rel_routed(
    E['系统用户'], 'left', E['求职申请'], 'top',
    l1='1', l2='n', label='提交',
    waypoints=[(route_x, E['系统用户']['left_cy']), (route_x, app_top[1])],
    label_l1_off=(E['系统用户']['left'] - 0.45, E['系统用户']['left_cy'] + 0.30),
    label_l2_off=(app_top[0] - 0.5, app_top[1] + 0.35),
    label_pos=(route_x - 0.35, (E['系统用户']['left_cy'] + app_top[1]) / 2),
)

# 4. 兼职分类 → 兼职信息: 1—n (属于) — vertical, bottom→top
draw_rel(E['兼职分类'], 'bottom', E['兼职信息'], 'top',
         l1='1', l2='n', label='属于')

# 5. 兼职信息 → 求职申请: 1—n (接收) — diagonal left-down
draw_rel(E['兼职信息'], 'bottom', E['求职申请'], 'top',
         l1='1', l2='n', label='接收')

# 6. 求职申请 → 工作订单: 1—1 (生成) — horizontal, right→left
draw_rel(E['求职申请'], 'right', E['工作订单'], 'left',
         l1='1', l2='1', label='生成')

# 7. 工作订单 → 评价信息: 1—1 (产生) — horizontal, right→left
draw_rel(E['工作订单'], 'right', E['评价信息'], 'left',
         l1='1', l2='1', label='产生')

# ═══════════════════════════════════════════════════════════════════════════
#  TITLE
# ═══════════════════════════════════════════════════════════════════════════
ax.text(11, 17.3, '校园兼职平台 E-R 实体关系图',
        fontsize=18, fontweight='bold', ha='center', va='center',
        color='black', zorder=10)

# ═══════════════════════════════════════════════════════════════════════════
#  SAVE
# ═══════════════════════════════════════════════════════════════════════════
plt.tight_layout(pad=3.0)
out_path = '/home/z/my-project/Xiao-Yi-Pin/download/thesis_images/er_diagram.png'
fig.savefig(out_path, dpi=150, bbox_inches='tight', facecolor='white', edgecolor='none')
plt.close(fig)
print(f"\n=== Saved → {out_path} ===")
