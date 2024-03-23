/**
 * 🪐 Inspectable Wrappers Specification provides a standard for wrapper chain with the inspection ability.
 *
 * <ul>
 *   <li>{@link io.foldright.inspectablewrappers.Wrapper} is core interface, used to
 *     <ul>
 *       <li>identify the wrapper instances as a wrapper chain
 *       <li>provide static entry methods to inspect the wrapper chain
 *     </ul>
 *   <li>{@link io.foldright.inspectablewrappers.Attachable} interface is used to
 *     enhance the wrapper instances with the attachment storage ability
 *   <li>{@link io.foldright.inspectablewrappers.WrapperAdapter} interface is used to
 *     adapt an existed wrapper without modifying it
 * </ul>
 *
 * @see io.foldright.inspectablewrappers.Wrapper
 * @see io.foldright.inspectablewrappers.Attachable
 * @see io.foldright.inspectablewrappers.WrapperAdapter
 */
package io.foldright.inspectablewrappers;
