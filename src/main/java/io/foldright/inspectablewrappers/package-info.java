/**
 * 🪐 Inspectable Wrappers Specification provides a standard for wrapper chain with the inspection ability.
 *
 * <ul>
 * <li>The specification interfaces:
 *   <ul>
 *   <li>{@link io.foldright.inspectablewrappers.Wrapper} interface is used to be implemented
 *       by wrapper classes, make an <strong>inspectable wrapper chain</strong>(linked list).
 *   <li>{@link io.foldright.inspectablewrappers.Attachable} interface interface is used to
 *       enhance the wrapper instances with the attachment storage ability
 *   <li>{@link io.foldright.inspectablewrappers.WrapperAdapter} interface is used to adapt
 *       an existed wrapper instance to type {@link io.foldright.inspectablewrappers.Wrapper} without modifying it.
 *   </ul>
 * <li>The {@link io.foldright.inspectablewrappers.Inspector} class is used to inspect the wrapper chain.
 * </ul>
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author Zava Xu (zava dot kid at gmail dot com)
 * @author Yang Fang (snoop dot fy at gmail dot com)
 * @see io.foldright.inspectablewrappers.Wrapper
 * @see io.foldright.inspectablewrappers.Attachable
 * @see io.foldright.inspectablewrappers.WrapperAdapter
 * @see io.foldright.inspectablewrappers.Inspector
 */
package io.foldright.inspectablewrappers;
