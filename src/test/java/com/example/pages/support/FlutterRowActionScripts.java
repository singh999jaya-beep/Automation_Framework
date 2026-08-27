package com.example.pages.support;

/**
 * Shared JavaScript helpers for locating Flutter row Delete/Block semantics nodes.
 */
final class FlutterRowActionScripts {

    private static final String ROW_ANCHOR_MATCHER =
            "    return label === 'user_row_email_0' || label === 'user_row_id_0' ||" +
                    "      label === 'company_row_status_0' || label.includes('company_row') ||" +
                    "      label.startsWith('VI-') || text.includes('@');";

    static final String DELETE_ROW_FIND_SCRIPT =
            "const deleteClickTarget = (node) => {" +
                    "  if (!node) return null;" +
                    "  let current = node;" +
                    "  let best = null;" +
                    "  while (current) {" +
                    "    if (current.hasAttribute && current.hasAttribute('flt-tappable')) {" +
                    "      const text = (current.textContent || '').trim();" +
                    "      if (text === 'Delete') return current;" +
                    "      if (!best && text.includes('Delete') && !text.includes('Block')) best = current;" +
                    "    }" +
                    "    current = current.parentElement;" +
                    "  }" +
                    "  if (best) return best;" +
                    "  const nearest = node.closest('flt-semantics[flt-tappable]');" +
                    "  return nearest || node;" +
                    "};" +
                    "const isTargetVisible = (target) => {" +
                    "  if (!target) return false;" +
                    "  const rect = target.getBoundingClientRect();" +
                    "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                    "  return !(rect.right < 0 || rect.left > window.innerWidth ||" +
                    "    rect.bottom < 0 || rect.top > window.innerHeight);" +
                    "};" +
                    "const findFirstRowDeleteNode = (semanticsLabel) => {" +
                    "  const byLabel = document.querySelector('flt-semantics[aria-label=\"' + semanticsLabel + '\"]');" +
                    "  if (byLabel) return byLabel;" +
                    "  const all = Array.from(document.querySelectorAll('flt-semantics'));" +
                    "  const rowAnchor = all.find(node => {" +
                    "    const label = (node.getAttribute('aria-label') || '');" +
                    "    const text = (node.textContent || '');" +
                    ROW_ANCHOR_MATCHER +
                    "  });" +
                    "  const rowDeletes = (scope) => Array.from(scope.querySelectorAll('flt-semantics')).filter(node => {" +
                    "    const text = (node.textContent || '').trim();" +
                    "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                    "    return text === 'Delete' && !label.includes('block') && !label.includes('confirm_delete');" +
                    "  });" +
                    "  if (!rowAnchor) {" +
                    "    const deletes = rowDeletes(document);" +
                    "    for (const del of deletes) {" +
                    "      if (isTargetVisible(deleteClickTarget(del))) return del;" +
                    "    }" +
                    "    return deletes[0] || null;" +
                    "  }" +
                    "  let ancestor = rowAnchor;" +
                    "  for (let depth = 0; depth < 14 && ancestor; depth++) {" +
                    "    const deletes = rowDeletes(ancestor);" +
                    "    if (deletes.length) {" +
                    "      const preferred = deletes.find(n => n.getAttribute('aria-label') === semanticsLabel);" +
                    "      const visible = deletes.find(n => isTargetVisible(deleteClickTarget(n)));" +
                    "      return preferred || visible || deletes[0];" +
                    "    }" +
                    "    ancestor = ancestor.parentElement;" +
                    "  }" +
                    "  return null;" +
                    "};";

    static final String BLOCK_ROW_FIND_SCRIPT =
            "const blockClickTarget = (node) => {" +
                    "  if (!node) return null;" +
                    "  let current = node;" +
                    "  while (current) {" +
                    "    if (current.hasAttribute && current.hasAttribute('flt-tappable')) {" +
                    "      const text = (current.textContent || '').trim();" +
                    "      if (text === 'Block') return current;" +
                    "    }" +
                    "    current = current.parentElement;" +
                    "  }" +
                    "  const nearest = node.closest('flt-semantics[flt-tappable]');" +
                    "  return nearest || node;" +
                    "};" +
                    "const isTargetVisible = (target) => {" +
                    "  if (!target) return false;" +
                    "  const rect = target.getBoundingClientRect();" +
                    "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                    "  return !(rect.right < 0 || rect.left > window.innerWidth ||" +
                    "    rect.bottom < 0 || rect.top > window.innerHeight);" +
                    "};" +
                    "const findFirstRowBlockNode = (semanticsLabel) => {" +
                    "  const byExact = document.querySelector('flt-semantics[aria-label=\"' + semanticsLabel + '\"]');" +
                    "  if (byExact) return byExact;" +
                    "  const byPrefix = Array.from(document.querySelectorAll('flt-semantics')).find(node => {" +
                    "    const label = (node.getAttribute('aria-label') || '');" +
                    "    const text = (node.textContent || '').trim();" +
                    "    if (text !== 'Block') return false;" +
                    "    if (label === semanticsLabel) return true;" +
                    "    const base = semanticsLabel.replace(/_\\d+$/, '');" +
                    "    return label.startsWith(base) || (label.includes('block_') && label.includes('button'));" +
                    "  });" +
                    "  if (byPrefix) return byPrefix;" +
                    "  const all = Array.from(document.querySelectorAll('flt-semantics'));" +
                    "  const rowAnchor = all.find(node => {" +
                    "    const label = (node.getAttribute('aria-label') || '');" +
                    "    const text = (node.textContent || '');" +
                    ROW_ANCHOR_MATCHER +
                    "  });" +
                    "  const rowBlocks = (scope) => Array.from(scope.querySelectorAll('flt-semantics')).filter(node => {" +
                    "    const text = (node.textContent || '').trim();" +
                    "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                    "    return text === 'Block' && !label.includes('confirm_block') && !label.includes('unblock');" +
                    "  });" +
                    "  if (!rowAnchor) {" +
                    "    const blocks = rowBlocks(document);" +
                    "    for (const block of blocks) {" +
                    "      if (isTargetVisible(blockClickTarget(block))) return block;" +
                    "    }" +
                    "    return blocks[0] || null;" +
                    "  }" +
                    "  let ancestor = rowAnchor;" +
                    "  for (let depth = 0; depth < 14 && ancestor; depth++) {" +
                    "    const blocks = rowBlocks(ancestor);" +
                    "    if (blocks.length) {" +
                    "      const preferred = blocks.find(n => n.getAttribute('aria-label') === semanticsLabel);" +
                    "      const visible = blocks.find(n => isTargetVisible(blockClickTarget(n)));" +
                    "      return preferred || visible || blocks[0];" +
                    "    }" +
                    "    ancestor = ancestor.parentElement;" +
                    "  }" +
                    "  return null;" +
                    "};";

    static final String UNBLOCK_ROW_FIND_SCRIPT =
            "const unblockClickTarget = (node) => {" +
                    "  if (!node) return null;" +
                    "  let current = node;" +
                    "  while (current) {" +
                    "    if (current.hasAttribute && current.hasAttribute('flt-tappable')) {" +
                    "      const text = (current.textContent || '').trim();" +
                    "      if (text === 'Unblock') return current;" +
                    "    }" +
                    "    current = current.parentElement;" +
                    "  }" +
                    "  const nearest = node.closest('flt-semantics[flt-tappable]');" +
                    "  return nearest || node;" +
                    "};" +
                    "const isTargetVisible = (target) => {" +
                    "  if (!target) return false;" +
                    "  const rect = target.getBoundingClientRect();" +
                    "  if (rect.width <= 0 || rect.height <= 0) return false;" +
                    "  return !(rect.right < 0 || rect.left > window.innerWidth ||" +
                    "    rect.bottom < 0 || rect.top > window.innerHeight);" +
                    "};" +
                    "const findFirstRowUnblockNode = (semanticsLabel) => {" +
                    "  const byLabel = document.querySelector('flt-semantics[aria-label=\"' + semanticsLabel + '\"]');" +
                    "  if (byLabel) return byLabel;" +
                    "  const all = Array.from(document.querySelectorAll('flt-semantics'));" +
                    "  const rowAnchor = all.find(node => {" +
                    "    const label = (node.getAttribute('aria-label') || '');" +
                    "    const text = (node.textContent || '');" +
                    ROW_ANCHOR_MATCHER +
                    "  });" +
                    "  const rowUnblocks = (scope) => Array.from(scope.querySelectorAll('flt-semantics')).filter(node => {" +
                    "    const text = (node.textContent || '').trim();" +
                    "    const label = (node.getAttribute('aria-label') || '').toLowerCase();" +
                    "    return text === 'Unblock' && !label.includes('confirm_unblock');" +
                    "  });" +
                    "  if (!rowAnchor) {" +
                    "    const unblocks = rowUnblocks(document);" +
                    "    for (const unblock of unblocks) {" +
                    "      if (isTargetVisible(unblockClickTarget(unblock))) return unblock;" +
                    "    }" +
                    "    return unblocks[0] || null;" +
                    "  }" +
                    "  let ancestor = rowAnchor;" +
                    "  for (let depth = 0; depth < 14 && ancestor; depth++) {" +
                    "    const unblocks = rowUnblocks(ancestor);" +
                    "    if (unblocks.length) {" +
                    "      const preferred = unblocks.find(n => n.getAttribute('aria-label') === semanticsLabel);" +
                    "      const visible = unblocks.find(n => isTargetVisible(unblockClickTarget(n)));" +
                    "      return preferred || visible || unblocks[0];" +
                    "    }" +
                    "    ancestor = ancestor.parentElement;" +
                    "  }" +
                    "  return null;" +
                    "};";

    private FlutterRowActionScripts() {
    }
}
