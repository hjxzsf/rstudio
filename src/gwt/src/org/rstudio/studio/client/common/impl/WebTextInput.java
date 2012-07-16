/*
 * WebTextInput.java
 *
 * Copyright (C) 2009-12 by RStudio, Inc.
 *
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.common.impl;

import org.rstudio.core.client.MessageDisplay.PromptWithOptionResult;
import org.rstudio.core.client.widget.Operation;
import org.rstudio.core.client.widget.ProgressIndicator;
import org.rstudio.core.client.widget.ProgressOperationWithInput;
import org.rstudio.core.client.widget.TextEntryModalDialog;
import org.rstudio.studio.client.common.TextInput;
import org.rstudio.studio.client.common.Value;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;

public class WebTextInput implements TextInput
{
   public void promptForText(String title,
                             String label,
                             String initialValue,
                             boolean usePasswordMask,
                             boolean numbersOnly,
                             int selectionStart,
                             int selectionLength,
                             String okButtonCaption,
                             ProgressOperationWithInput<String> okOperation,
                             Operation cancelOperation)
   {
      new TextEntryModalDialog(title,
                               label,
                               initialValue,
                               usePasswordMask,
                               null,
                               false,
                               numbersOnly,
                               selectionStart,
                               selectionLength,
                               okButtonCaption,
                               300,
                               okOperation,
                               cancelOperation).showModal();
   }

   @Override
   public void promptForTextWithOption(
                                 String title,
                                 String label,
                                 String initialValue,
                                 boolean usePasswordMask,
                                 String extraOptionPrompt,
                                 boolean extraOptionDefault,
                                 int selectionStart,
                                 int selectionLength,
                                 String okButtonCaption,
                                 final ProgressOperationWithInput<PromptWithOptionResult> okOperation,
                                 Operation cancelOperation)
   {
      // This variable introduces a level of pointer indirection that lets us
      // get around passing TextEntryModalDialog a reference to itself in its
      // own constructor.
      final Value<TextEntryModalDialog> pDialog = new Value<TextEntryModalDialog>(null);

      final TextEntryModalDialog dialog = new TextEntryModalDialog(
            title,
            label,
            initialValue,
            usePasswordMask,
            extraOptionPrompt,
            extraOptionDefault,
            false,
            selectionStart,
            selectionLength,
            okButtonCaption,
            300,
            new ProgressOperationWithInput<String>()
            {
               @Override
               public void execute(String input, ProgressIndicator indicator)
               {
                  PromptWithOptionResult result = new PromptWithOptionResult();
                  result.input = input;
                  result.extraOption = pDialog.getValue().getExtraOption();
                  okOperation.execute(result, indicator);
               }
            },
            cancelOperation)  {
         
            @Override
            protected void positionAndShowDialog(final Command onCompleted)
            {
               setPopupPositionAndShow(new PositionCallback() {
   
                  @Override
                  public void setPosition(int offsetWidth, int offsetHeight)
                  {
                     int left = (Window.getClientWidth()/2) - (offsetWidth/2);
                     int top = 50;
                     
                     setPopupPosition(left, top);
                     
                     onCompleted.execute();
                  }
                  
               });
            }
         
      };

      pDialog.setValue(dialog, false);

      dialog.showModal();
   }
}
