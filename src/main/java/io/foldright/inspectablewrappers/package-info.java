/**
 * 🪐 Inspectable Wrappers Specification provides a standard for wrapper chain with the inspection ability.
 *
 * <ul>
 *   <li>{@link io.foldright.inspectablewrappers.Wrapper} is core interface, used to
 *   be implemented by wrapper classes, make an <strong>inspectable wrapper chain</strong>(linked list).
 *   <li>{@link io.foldright.inspectablewrappers.Attachable} interface is used to
 *     enhance the wrapper instances with the attachment storage ability
 *   <li>{@link io.foldright.inspectablewrappers.WrapperAdapter} interface is used to
 *       adapt an existed wrapper instance to type {@link io.foldright.inspectablewrappers.Wrapper} without modifying it.
 * </ul>
 * <p>
 * The {@link io.foldright.inspectablewrappers.Inspector} class is used to inspect the wrapper chain.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see io.foldright.inspectablewrappers.Wrapper
 * @see io.foldright.inspectablewrappers.Attachable
 * @see io.foldright.inspectablewrappers.WrapperAdapter
 * @see io.foldright.inspectablewrappers.Inspector
 */
package io.foldright.inspectablewrappers;
