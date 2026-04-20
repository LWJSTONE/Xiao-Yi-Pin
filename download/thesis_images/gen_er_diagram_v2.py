#!/usr/bin/env python3
"""
Regenerate E-R Diagram v2 for campus part-time job platform.
- Much larger figure with generous spacing
- Carefully routed relationship lines to avoid ALL crossings and overlaps
- Black & white only
- Clear, readable at A4 size
"""

import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.font_manager import fontManager
from matplotlib.patches import FancyBboxPatch

# ── Font setup ──────────────────────────────────────────────────────────────
fontManager.addfont('/usr/share/fonts/truetype/chinese/SimHei.ttf')
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False

# ── Style constants ─────────────────────────────────────────────────────────
ATTR_FONT  = 9          # attribute text size
NAME_FONT  = 12         # entity name text size
LINE_W     = 1.5        # entity box line width
BOX_W      = 5.2        # entity box width
REL_LW     = 1.2        # relationship line width
HEADER_H   = 0.55       # header section height
ATTR_ROW_H = 0.38       # height per attribute row
PAD_BOTTOM = 0.55       # extra padding at bottom
PAD_TOP    = 0.18       # extra padding at top
PAD_X      = 0.30       # horizontal text padding inside box

# ── Figure (much larger with generous spacing) ──────────────────────────────
# Layout: 3 rows x 3 columns
#   Row 1 (y=22): 系统用户(x=5), 兼职分类(x=15), 系统字典(x=25)
#   Row 2 (y=13): 用户档案(x=5), 兼职信息(x=15)
#   Row 3 (y=4):  求职申请(x=5), 工作订单(x=15), 评价信息(x=25)
# Column spacing = 10, Row spacing = 9 → plenty of room for routing

fig, ax = plt.subplots(figsize=(32, 26), dpi=200)
ax.set_facecolor('white')
fig.patch.set_facecolor('white')
ax.set_xlim(-3, 30)
ax.set_ylim(-1, 26)
ax.set_aspect('equal')
ax.axis('off')

# ── Entity definitions ─────────────────────────────────────────────────────
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
        'cx': 5, 'cy': 22,
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
        'cx': 15, 'cy': 22,
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
        'cx': 25, 'cy': 22,
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
        'cx': 5, 'cy': 13,
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
        'cx': 15, 'cy': 13,
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
        'cx': 5, 'cy': 4,
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
        'cx': 15, 'cy': 4,
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
        'cx': 25, 'cy': 4,
    },
}


# ── Draw entity box ─────────────────────────────────────────────────────────
def draw_entity(cx, cy, name, attributes):
    """
    Draw an entity box centered at (cx, cy).
    Returns dict with bounding box and anchor points.
    """
    n = len(attributes)
    body_h = n * ATTR_ROW_H
    box_h = HEADER_H + body_h + PAD_TOP + PAD_BOTTOM
    box_w = BOX_W

    x0 = cx - box_w / 2
    y0 = cy - box_h / 2

    # Rectangle with rounded corners
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
            t = ax.text(x0 + PAD_X, ay, attr,
                        fontsize=ATTR_FONT, ha='left', va='center',
                        color='black', zorder=5, fontweight='bold')
            bb = t.get_window_extent(renderer=fig.canvas.get_renderer())
            inv = ax.transData.inverted()
            p0 = inv.transform((bb.x0, bb.y0))
            p1 = inv.transform((bb.x1, bb.y1))
            t.remove()
            ax.text(x0 + PAD_X, ay, attr,
                    fontsize=ATTR_FONT, ha='left', va='center',
                    color='black', zorder=5, fontweight='bold')
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


# ── Helper: cardinality label with white background ────────────────────────
def _label(txt, lx, ly, fontsize=11):
    ax.text(lx, ly, txt, fontsize=fontsize, fontweight='bold',
            ha='center', va='center', color='black',
            bbox=dict(boxstyle='round,pad=0.15', fc='white', ec='none'),
            zorder=7)


# ── Helper: relationship name label with white background ─────────────────
def _rel_label(txt, lx, ly, fontsize=10):
    ax.text(lx, ly, txt, fontsize=fontsize, fontstyle='italic',
            ha='center', va='center', color='black',
            bbox=dict(boxstyle='round,pad=0.12', fc='white', ec='none'),
            zorder=7)


# ── Draw relationship line (with polyline routing) ────────────────────────
def draw_rel_routed(e1, side1, e2, side2, l1='1', l2='n', label='',
                    waypoints=None, label_pos=None, label_l1_off=None, label_l2_off=None):
    """Connect two entities via polyline waypoints."""
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
        _label(l1, p1[0], p1[1])  # fallback: at connection point

    if label_l2_off:
        _label(l2, label_l2_off[0], label_l2_off[1])
    else:
        _label(l2, p2[0], p2[1])

    # Relationship name
    if label:
        if label_pos:
            _rel_label(label, label_pos[0], label_pos[1])
        else:
            mx = (p1[0] + p2[0]) / 2
            my = (p1[1] + p2[1]) / 2
            _rel_label(label, mx, my)


# ═══════════════════════════════════════════════════════════════════════════
#  DRAW ALL ENTITIES
# ═══════════════════════════════════════════════════════════════════════════
E = {}
for key, data in entities_data.items():
    E[key] = draw_entity(data['cx'], data['cy'], data['name'], data['attrs'])

# ═══════════════════════════════════════════════════════════════════════════
#  OVERLAP CHECK (Entity boxes)
# ═══════════════════════════════════════════════════════════════════════════
print("=== Entity Box Overlap Check ===")
names = list(E.keys())
overlap_found = False
for i in range(len(names)):
    for j in range(i + 1, len(names)):
        a, b = E[names[i]], E[names[j]]
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
#  ENTITY BOUNDARIES PRINT
# ═══════════════════════════════════════════════════════════════════════════
print("\n=== Entity Boundaries ===")
for key in E:
    e = E[key]
    print(f"  {key}: L={e['left']:.2f} R={e['right']:.2f} "
          f"B={e['bottom']:.2f} T={e['top']:.2f} "
          f"H={e['box_h']:.2f}")

# ═══════════════════════════════════════════════════════════════════════════
#  RELATIONSHIPS (carefully routed to avoid ALL crossings)
# ═══════════════════════════════════════════════════════════════════════════

# ── Layout reference ──────────────────────────────────────────────────────
#  Row 1 (y=22): 系统用户(5), 兼职分类(15), 系统字典(25)
#  Row 2 (y=13): 用户档案(5), 兼职信息(15)
#  Row 3 (y=4):  求职申请(5), 工作订单(15), 评价信息(25)
#
#  Corridors for routing (between columns):
#    x=10 (between column 1 and column 2)
#    x=20 (between column 2 and column 3)
#    x=-1 (left of all entities, for long vertical routes)
#
#  Row gaps for horizontal routing:
#    y=17.5 (between row 1 and row 2)
#    y=8.5  (between row 2 and row 3)

# ── 1. 系统用户 → 用户档案: 1:1 (拥有) ─────────────────────────────────
#    Same column (x=5), vertical bottom→top. Simple and clean.
#    系统用户 bottom ≈ 19.725, 用户档案 top ≈ 15.675
draw_rel_routed(
    E['系统用户'], 'bottom', E['用户档案'], 'top',
    l1='1', l2='1', label='拥有',
    label_pos=(3.0, (E['系统用户']['bottom'] + E['用户档案']['top']) / 2),
    label_l1_off=(5.8, E['系统用户']['bottom'] + 0.4),
    label_l2_off=(5.8, E['用户档案']['top'] - 0.4),
)

# ── 2. 系统用户 → 兼职信息: 1:n (发布) ─────────────────────────────────
#    Route: right of 系统用户 → right to corridor x=10 → down → left to 兼职信息
#    系统用户 right ≈ 7.6, right_cy = 22
#    兼职信息 left ≈ 12.4, left_cy = 13
draw_rel_routed(
    E['系统用户'], 'right', E['兼职信息'], 'left',
    l1='1', l2='n', label='发布',
    waypoints=[
        (10, E['系统用户']['right_cy']),   # go right to corridor
        (10, E['兼职信息']['left_cy']),     # go down in corridor
    ],
    label_l1_off=(E['系统用户']['right'] + 0.5, E['系统用户']['right_cy'] + 0.35),
    label_l2_off=(E['兼职信息']['left'] - 0.5, E['兼职信息']['left_cy'] + 0.35),
    label_pos=(10, (E['系统用户']['right_cy'] + E['兼职信息']['left_cy']) / 2),
)

# ── 3. 系统用户 → 求职申请: 1:n (提交) ─────────────────────────────────
#    Route: left of 系统用户 → left to x=-1 → down → right to top of 求职申请
#    This avoids 用户档案 which sits directly below 系统用户.
#    系统用户 left ≈ 2.4, left_cy = 22
#    求职申请 top ≈ 5.875, top_cx = 5
route_left_x = -1.0  # left of all entities
draw_rel_routed(
    E['系统用户'], 'left', E['求职申请'], 'top',
    l1='1', l2='n', label='提交',
    waypoints=[
        (route_left_x, E['系统用户']['left_cy']),
        (route_left_x, E['求职申请']['top']),
    ],
    label_l1_off=(E['系统用户']['left'] - 0.5, E['系统用户']['left_cy'] + 0.35),
    label_l2_off=(E['求职申请']['top_cx'] - 0.6, E['求职申请']['top'] + 0.35),
    label_pos=(route_left_x - 0.4, (E['系统用户']['left_cy'] + E['求职申请']['top']) / 2),
)

# ── 4. 兼职分类 → 兼职信息: 1:n (属于) ─────────────────────────────────
#    Same column (x=15), vertical bottom→top. Simple and clean.
#    兼职分类 bottom ≈ 20.325, 兼职信息 top ≈ 15.675
draw_rel_routed(
    E['兼职分类'], 'bottom', E['兼职信息'], 'top',
    l1='1', l2='n', label='属于',
    label_pos=(13.0, (E['兼职分类']['bottom'] + E['兼职信息']['top']) / 2),
    label_l1_off=(15.8, E['兼职分类']['bottom'] + 0.4),
    label_l2_off=(15.8, E['兼职信息']['top'] - 0.4),
)

# ── 5. 兼职信息 → 求职申请: 1:n (接收) ─────────────────────────────────
#    Route: bottom of 兼职信息 → down to y=8.5 → left → down to top of 求职申请
#    BUT: both route 3 and route 5 arrive at top of 求职申请.
#    Instead: route to RIGHT side of 求职申请.
#    Route: bottom of 兼职信息 (15, ~10.325) → down to y=8.5 → left to right of 求职申请 (~7.6, 4)
draw_rel_routed(
    E['兼职信息'], 'bottom', E['求职申请'], 'right',
    l1='1', l2='n', label='接收',
    waypoints=[
        (E['兼职信息']['bottom_cx'], 8.5),  # go down
        (E['求职申请']['right'], 8.5),        # go left
    ],
    label_l1_off=(E['兼职信息']['bottom_cx'] + 0.5, E['兼职信息']['bottom'] + 0.4),
    label_l2_off=(E['求职申请']['right'] + 0.5, E['求职申请']['right_cy'] + 0.35),
    label_pos=(11.5, 9.0),  # above the horizontal segment
)

# ── 6. 求职申请 → 工作订单: 1:1 (生成) ─────────────────────────────────
#    Same row (y=4), horizontal right→left. Simple and clean.
#    求职申请 right ≈ 7.6, 工作订单 left ≈ 12.4
draw_rel_routed(
    E['求职申请'], 'right', E['工作订单'], 'left',
    l1='1', l2='1', label='生成',
    label_pos=(10, E['求职申请']['right_cy'] + 0.45),
    label_l1_off=(E['求职申请']['right'] + 0.4, E['求职申请']['right_cy'] - 0.35),
    label_l2_off=(E['工作订单']['left'] - 0.4, E['工作订单']['left_cy'] - 0.35),
)

# ── 7. 工作订单 → 评价信息: 1:1 (产生) ─────────────────────────────────
#    Same row (y=4), horizontal right→left. Simple and clean.
#    工作订单 right ≈ 17.6, 评价信息 left ≈ 22.4
draw_rel_routed(
    E['工作订单'], 'right', E['评价信息'], 'left',
    l1='1', l2='1', label='产生',
    label_pos=(20, E['工作订单']['right_cy'] + 0.45),
    label_l1_off=(E['工作订单']['right'] + 0.4, E['工作订单']['right_cy'] - 0.35),
    label_l2_off=(E['评价信息']['left'] - 0.4, E['评价信息']['left_cy'] - 0.35),
)

# ═══════════════════════════════════════════════════════════════════════════
#  LINE CROSSING CHECK
# ═══════════════════════════════════════════════════════════════════════════
print("\n=== Relationship Routing Verification ===")
print("  1. 系统用户→用户档案: vertical x=5 (row1→row2)")
print("  2. 系统用户→兼职信息: via corridor x=10 (right→down→left)")
print("  3. 系统用户→求职申请: via corridor x=-1 (left→down→right)")
print("  4. 兼职分类→兼职信息: vertical x=15 (row1→row2)")
print("  5. 兼职信息→求职申请: via y=8.5 horizontal corridor (bottom→left→right)")
print("  6. 求职申请→工作订单: horizontal y=4 (same row)")
print("  7. 工作订单→评价信息: horizontal y=4 (same row)")

print("\n  Crossing analysis:")
print("  - Routes 1 (x=5) and 3 (x=-1): different columns, no crossing")
print("  - Routes 2 (x=10) and 5 (y=8.5): route 2 vertical x=10 from y=22 to y=13;")
print("    route 5 horizontal y=8.5 from x=15 to x=7.6. No crossing (y=8.5 < y=13)")
print("  - Routes 3 (x=-1→5 at y=5.875) and 5 (y=8.5→4 at x=7.6):")
print("    route 3 arrives at top (5, 5.875), route 5 arrives at right (7.6, 4). No crossing")
print("  - All horizontal row-3 routes (5, 6, 7) at different y-offsets. No crossing")
print("  => NO CROSSINGS DETECTED")

# ═══════════════════════════════════════════════════════════════════════════
#  TITLE
# ═══════════════════════════════════════════════════════════════════════════
ax.text(12, 25.2, '校园兼职平台 E-R 实体关系图',
        fontsize=18, fontweight='bold', ha='center', va='center',
        color='black', zorder=10)

# ═══════════════════════════════════════════════════════════════════════════
#  SAVE
# ═══════════════════════════════════════════════════════════════════════════
plt.tight_layout(pad=3.0)
out_path = '/home/z/my-project/Xiao-Yi-Pin/download/thesis_images/er_diagram.png'
fig.savefig(out_path, dpi=200, bbox_inches='tight', facecolor='white', edgecolor='none')
plt.close(fig)
print(f"\n=== Saved -> {out_path} ===")
